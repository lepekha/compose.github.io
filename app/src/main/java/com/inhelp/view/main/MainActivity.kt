package com.inhelp.view.mainimport android.Manifestimport android.graphics.Colorimport android.os.Bundleimport android.view.WindowManagerimport androidx.core.content.ContentProviderCompat.requireContextimport androidx.core.view.ViewCompatimport androidx.core.view.isVisibleimport androidx.core.view.updatePaddingimport androidx.recyclerview.widget.RecyclerViewimport backimport clearAllFragmentsimport com.dali.file.FileStorageimport com.eazypermissions.common.model.PermissionResultimport com.eazypermissions.dsl.PermissionManagerimport com.inhelp.Rimport com.inhelp.base.mvp.BaseMvpActivityimport com.inhelp.base.mvp.BaseMvpViewimport com.inhelp.dialogs.main.DialogManagerimport com.inhelp.extension.*import com.inhelp.view.customView.SpanningLinearLayoutManagerimport com.inhelp.view.main.main.FragmentMainimport data.Menuimport kotlinx.android.synthetic.main.activity_main.*import org.koin.android.ext.android.getKoinimport org.koin.android.ext.android.injectimport org.koin.core.qualifier.namedimport replaceimport java.lang.ref.WeakReferenceclass MainActivity : BaseMvpActivity<MainView, MainPresenter>(), MainView {    override val presenter: MainPresenter by lazy {        val scope = getKoin().getOrCreateScope(                "app", named("app"))        scope.get()    }    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        DialogManager.init(context = WeakReference(this))        FileStorage.init(context = this)        setContentView(R.layout.activity_main)        bottomMenu.isEnabled = false        alert.setVibrate(EVibrate.BUTTON)        window.navigationBarColor = getCurrentContext().getColorFromAttr(R.attr.color_1)        supportFragmentManager.replace(fragment = FragmentMain(), addToBackStack = true)        btnBack.setOnClickListener {            onBackPressed()        }    }    private var currentBottomMenu = mutableListOf<Menu>()    override fun setupBottomMenu(menu: MutableList<Menu>) {        currentBottomMenu = menu        if(menu.isEmpty()){            if(bottomMenu.isEnabled != menu.isNotEmpty()){                bottomMenu.isEnabled = false                bottomMenu.animateMargin(bottom = -(55.dp))            }        }else{            bottomMenu.layoutManager = SpanningLinearLayoutManager(getCurrentContext(), RecyclerView.HORIZONTAL, false)            bottomMenu.adapter = BottomMenuRvAdapter(menu = menu)            if(bottomMenu.isEnabled != menu.isNotEmpty()){                bottomMenu.isEnabled = true                bottomMenu.animateMargin(bottom = 55.dp)            }        }    }    override fun setVisibleBack(isVisible: Boolean) {        btnBack.isVisible = isVisible    }    override fun setTitle(title: String) {        txtTitle.text = title    }    private val hideRunnable = Runnable {        alert.setOnClickListener(null)        alert.setClickable(false)        alert.animateMargin(top = (-75).dp)    }    override fun showAlert(srtResId: Int) {        txtAlert.setText(srtResId)        if(!alert.isClickable){            alert.removeCallbacks(hideRunnable)            alert.animateMargin(top = 75.dp)            alert.postDelayed(hideRunnable, 2000)            alert.setOnClickListener {                alert.removeCallbacks(hideRunnable)                alert.post(hideRunnable)            }        }else{            alert.removeCallbacks(hideRunnable)            alert.postDelayed(hideRunnable, 2000)        }    }    override fun backPress() : Boolean {        val backInFragment = (supportFragmentManager.fragments.lastOrNull() as? BaseMvpView)?.backPress() ?: false        if( !backInFragment && !supportFragmentManager.back()){            finishApplication()        }        return true    }    override fun updateBottomMenu() {        bottomMenu.adapter = BottomMenuRvAdapter(menu = currentBottomMenu.filter { it.isVisible }.toMutableList())        bottomMenu.adapter?.notifyDataSetChanged()    }    override fun onBackPressed() {        backPress()    }    override fun backToMain() {        supportFragmentManager.clearAllFragments()        supportFragmentManager.replace(fragment = FragmentMain(), addToBackStack = true)    }    override fun onResume() {        super.onResume()    }    override fun bottomSheetCreate(){        this.window.navigationBarColor = this.getColorFromAttr(com.inhelp.gallery.R.attr.color_5)    }    override fun bottomSheetDestroy(){        this.window.navigationBarColor = this.getColorFromAttr(com.inhelp.gallery.R.attr.color_1)    }    override fun finishApplication() {        finish()    }    override fun onDestroy() {        super.onDestroy()        getKoin().getScopeOrNull("app")?.close()    }}