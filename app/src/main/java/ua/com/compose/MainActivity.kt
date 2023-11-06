package ua.com.composeimport android.content.Intentimport android.net.Uriimport android.os.Bundleimport android.os.Parcelableimport androidx.compose.animation.EnterTransitionimport androidx.compose.animation.ExitTransitionimport androidx.compose.foundation.backgroundimport androidx.compose.foundation.layout.Columnimport androidx.compose.foundation.layout.Rowimport androidx.compose.foundation.layout.WindowInsetsimport androidx.compose.foundation.layout.fillMaxSizeimport androidx.compose.foundation.layout.fillMaxWidthimport androidx.compose.foundation.layout.paddingimport androidx.compose.foundation.layout.sizeimport androidx.compose.foundation.layout.statusBarsimport androidx.compose.foundation.layout.windowInsetsPaddingimport androidx.compose.foundation.layout.wrapContentHeightimport androidx.compose.material3.FilledTonalIconButtonimport androidx.compose.material3.Iconimport androidx.compose.material3.IconButtonimport androidx.compose.material3.IconButtonColorsimport androidx.compose.material3.Textimport androidx.compose.runtime.getValueimport androidx.compose.runtime.mutableStateOfimport androidx.compose.runtime.rememberimport androidx.compose.runtime.setValueimport androidx.compose.ui.Alignmentimport androidx.compose.ui.Modifierimport androidx.compose.ui.graphics.Colorimport androidx.compose.ui.platform.ComposeViewimport androidx.compose.ui.platform.LocalViewimport androidx.compose.ui.platform.ViewCompositionStrategyimport androidx.compose.ui.res.colorResourceimport androidx.compose.ui.res.painterResourceimport androidx.compose.ui.res.stringResourceimport androidx.compose.ui.text.font.FontWeightimport androidx.compose.ui.text.style.TextAlignimport androidx.compose.ui.unit.dpimport androidx.compose.ui.unit.spimport androidx.compose.ui.zIndeximport androidx.core.splashscreen.SplashScreen.Companion.installSplashScreenimport androidx.core.view.WindowCompatimport androidx.navigation.compose.NavHostimport androidx.navigation.compose.composableimport androidx.navigation.compose.rememberNavControllerimport com.google.android.material.bottomsheet.BottomSheetDialogFragmentimport org.koin.android.scope.AndroidScopeComponentimport org.koin.androidx.compose.koinViewModelimport org.koin.androidx.scope.ScopeActivityimport ua.com.compose.data.ColorNamesimport ua.com.compose.extension.EVibrateimport ua.com.compose.extension.clearAllFragmentsimport ua.com.compose.extension.vibrateimport ua.com.compose.screens.EPanelimport ua.com.compose.screens.camera.CameraScreenimport ua.com.compose.screens.image.ImageScreenimport ua.com.compose.screens.palette.PaletteScreenimport ua.com.compose.screens.settings.SettingsScreenclass MainActivity: ScopeActivity(), AndroidScopeComponent {    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        installSplashScreen()        ColorNames.init(this)        WindowCompat.setDecorFitsSystemWindows(window, false)        setContentView(ComposeView(this).apply {            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)            setContent {                val view = LocalView.current                val navController = rememberNavController()                var activePanel by remember { mutableStateOf(Settings.startScreen) }                var stateSettings by remember { mutableStateOf(false) }                if(stateSettings) {                    SettingsScreen {                        stateSettings = false                    }                }                Column(modifier = Modifier                    .windowInsetsPadding(WindowInsets.statusBars)                    .fillMaxSize()                ) {                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier                        .zIndex(1f)                        .background(colorResource(R.color.color_main_header))                        .padding(start = 16.dp, top = 8.dp, end = 8.dp)) {                        Text(                            text = stringResource(id = R.string.app_name),                            fontSize = 32.sp,                            fontWeight = FontWeight(700),                            color = colorResource(id = R.color.color_night_5),                            modifier = Modifier.weight(1f),                        )                        IconButton(onClick = { stateSettings = true }) {                            Icon(painter = painterResource(id = R.drawable.ic_settings),                                modifier = Modifier                                    .size(32.dp)                                    .padding(4.dp),                                tint = colorResource(id = R.color.color_night_9),                                contentDescription = null                            )                        }                    }                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier                        .zIndex(1f)                        .background(colorResource(R.color.color_main_header))                        .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 24.dp)) {                        EPanel.values().filter { it.isVisible() }.forEach { panel ->                            val colorSelected = IconButtonColors(                                containerColor = colorResource(id = R.color.color_button_background),                                contentColor = colorResource(id = R.color.color_night_9),                                disabledContainerColor = colorResource(id = R.color.color_button_background),                                disabledContentColor = colorResource(id = R.color.color_night_9)                            )                            val colorUnselected = IconButtonColors(                                containerColor = Color.Transparent,                                contentColor = colorResource(id = R.color.color_night_9),                                disabledContainerColor = colorResource(id = R.color.color_button_background),                                disabledContentColor = colorResource(id = R.color.color_night_9)                            )                            val background = if(activePanel == panel) {                                colorSelected                            } else {                                colorUnselected                            }                            Column(modifier = Modifier                                .wrapContentHeight()                                .weight(1f)) {                                FilledTonalIconButton(                                    onClick = {                                        view.vibrate(EVibrate.BUTTON)                                        activePanel = panel                                        Settings.startScreen = panel                                        navController.navigate(panel.rout)                                    },                                    colors = background,                                    modifier = Modifier.fillMaxWidth()                                ) {                                    val icon = if(activePanel == panel) {                                        painterResource(id = panel.iconResId)                                    } else {                                        painterResource(id = panel.iconFilledResId)                                    }                                    Icon(painter = icon,                                        modifier = Modifier                                            .size(32.dp)                                            .padding(4.dp),                                        tint = colorResource(id = R.color.color_night_9),                                        contentDescription = null                                    )                                }                                Text(                                    modifier = Modifier.fillMaxWidth(),                                    textAlign = TextAlign.Center,                                    text = stringResource(id = panel.titleResId),                                    color = colorResource(id = R.color.color_night_9),                                    fontSize = 15.sp,                                    fontWeight = FontWeight(600))                            }                        }                    }                    NavHost(                        modifier = Modifier                            .fillMaxSize()                            .zIndex(0f)                            .background(                                color = colorResource(                                    id = R.color.color_main_background                                )                            ),                        navController = navController,                        startDestination = activePanel.rout,                        enterTransition = {                            EnterTransition.None                                          },                        popEnterTransition = {                            EnterTransition.None                                          },                        exitTransition = {                            ExitTransition.None                        }                    ) {                        composable(EPanel.CAMERA.rout) { CameraScreen(viewModule = koinViewModel()) }                        composable(EPanel.IMAGE.rout) { ImageScreen(viewModule = koinViewModel()) }                        composable(EPanel.PALLETS.rout) { PaletteScreen(viewModule = koinViewModel()) }                    }                }            }        })    }    override fun onNewIntent(intent: Intent?) {        super.onNewIntent(intent)        if(intent?.action == Intent.ACTION_SEND) {            closeAllDialogs()            if (intent.type?.startsWith("image/") == true) {                handleSendImage(intent)            }        }    }    private fun handleSendImage(intent: Intent) {        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let { uri ->            closeAllDialogs()            supportFragmentManager.clearAllFragments()//            supportFragmentManager.replace(fragment = ColorPickFragment.newInstance(uri), containerId = R.id.id_fragment_container, addToBackStack = true)        }    }    private fun closeAllDialogs(){        this.supportFragmentManager.fragments.filterIsInstance<BottomSheetDialogFragment>().forEach {            it.dismissAllowingStateLoss()        }    }//    override fun onBackPressed() {//        finish()//    }}