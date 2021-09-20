package ua.com.compose.other_text_style.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType.Companion.Text
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ua.com.compose.mvp.BaseMvpFragment
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ua.com.compose.ThemeCompose
import ua.com.compose.extension.clipboardCopy
import ua.com.compose.extension.onTextChangedListener
import ua.com.compose.mvp.BaseMvvmFragment
import ua.com.compose.other_text_style.R
import ua.com.compose.other_text_style.databinding.ModuleOtherTextStyleFragmentTextStyleBinding
import ua.com.compose.other_text_style.utils.RecyclerItemDivider


@ExperimentalAnimationApi
class TextStyleFragment : BaseMvvmFragment() {

    companion object {
        fun newInstance(): TextStyleFragment {
            return TextStyleFragment()
        }
    }

    private var binding: ModuleOtherTextStyleFragmentTextStyleBinding? = null

    private val viewModel: TextStyleViewModel by viewModel()

    private val dividerItemDecoration by lazy {
        RecyclerItemDivider(ContextCompat.getDrawable(requireContext(), R.drawable.module_other_text_style_list_divider), dividerMargin = 0)
    }
    private fun initList() {
        binding?.list?.layoutManager = LinearLayoutManager(getCurrentContext(), RecyclerView.VERTICAL, false)
        binding?.list?.addItemDecoration(dividerItemDecoration)
        binding?.list?.adapter = MenuRvAdapter {
            requireContext().clipboardCopy(text = it)
            showAlert(R.string.module_other_text_style_fragment_text_copied)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ModuleOtherTextStyleFragmentTextStyleBinding.inflate(inflater)
        return binding?.root
    //        return ComposeView(requireContext()).apply {
//            setContent {
//                ThemeCompose(darkTheme = true) {
//                    val items = viewModel.strings.observeAsState()
//                    val isVisibleClearText = viewModel.isVisibleClearText.observeAsState()
//                    Surface(color = MaterialTheme.colors.background) {
//                        Column {
//                            val text = remember { mutableStateOf(TextFieldValue(text = "")) }
//                            TextField(
//                                value = text.value,
//                                singleLine = true,
//                                placeholder = { Text("Enter text") },
//                                onValueChange = {
//                                    text.value = it
//                                    viewModel.enterText(value = it.text)
//                                },
//                                colors = TextFieldDefaults.textFieldColors(
//                                    backgroundColor = MaterialTheme.colors.primaryVariant,
//                                    focusedIndicatorColor = Color.Transparent,
//                                    unfocusedIndicatorColor = Color.Transparent,
//                                    disabledIndicatorColor = Color.Transparent
//                                ),
//                                trailingIcon = {
//                                    if(isVisibleClearText.value == true) {
//                                        Icon(painter = painterResource(id = R.drawable.ic_close),
//                                            contentDescription = "clear",
//                                            tint = MaterialTheme.colors.onPrimary,
//                                            modifier = Modifier
//                                                .clickable {
//                                                    text.value = text.value.copy(text = "")
//                                                    viewModel.enterText(value = "")
//                                                })
//                                    }
//                                    },
//                                textStyle = TextStyle(color = MaterialTheme.colors.onPrimary, fontSize = 20.sp),
//                                shape = RoundedCornerShape(8.dp),
//                                modifier = Modifier
//                                    .padding(10.dp)
//                                    .fillMaxWidth()
//                                    .height(60.dp))
//                            Spacer(modifier = Modifier.padding(10.dp, 10.dp))
//                        LazyColumn {
//                            items.value?.forEach {
//                                item {
//                                    Text(
//                                        text = it,
//                                        color = MaterialTheme.colors.onPrimary,
//                                        modifier = Modifier.padding(10.dp, 10.dp).clickable {
//                                            requireContext().clipboardCopy(text = it)
//                                            showAlert(R.string.module_other_text_style_fragment_text_copied)
//                                        }
//                                    )
//                                }
//                            }
//                        }
//                        }
//                    }
//                }
//            }
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.module_other_text_style_fragment_title_text_style))
        initList()

        viewModel.isVisibleClearText.observe(this) {
            binding?.btnClear?.isVisible = it
        }

        viewModel.isVisiblePlaceHolder.observe(this) {
            binding?.imgPlaceholder?.isVisible = it
        }

        viewModel.strings.observe(this) {
            (binding?.list?.adapter as? MenuRvAdapter)?.update(newList = it)
        }

        binding?.editText?.onTextChangedListener {
            viewModel.enterText(it)
        }

        binding?.btnClear?.setOnClickListener {
            binding?.editText?.text?.clear()
            viewModel.enterText("")
        }
    }
}
