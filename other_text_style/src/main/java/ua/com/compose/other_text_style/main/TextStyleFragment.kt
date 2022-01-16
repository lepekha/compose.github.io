package ua.com.compose.other_text_style.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.koin.androidx.viewmodel.ext.android.viewModel
import ua.com.compose.extension.clipboardCopy
import ua.com.compose.extension.onTextChangedListener
import ua.com.compose.mvp.BaseMvvmFragment
import ua.com.compose.other_text_style.R
import ua.com.compose.other_text_style.databinding.ModuleOtherTextStyleFragmentTextStyleBinding
import ua.com.compose.other_text_style.utils.RecyclerItemDivider


class TextStyleFragment : BaseMvvmFragment() {

    companion object {
        fun newInstance(): TextStyleFragment {
            return TextStyleFragment()
        }
    }

    private var binding: ModuleOtherTextStyleFragmentTextStyleBinding? = null

    private val viewModel: TextStyleViewModel by viewModel()

    private fun initList() {
        binding?.list?.layoutManager = LinearLayoutManager(getCurrentContext(), RecyclerView.VERTICAL, false)
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
        enterText("")

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
            enterText(it)
        }

        binding?.btnClear?.setOnClickListener {
            binding?.editText?.text?.clear()
            enterText("")
        }
    }

    private fun enterText(value: String){
        if(value.isEmpty() or value.isBlank()){
            viewModel.enterText(requireContext().getString(R.string.module_other_text_style_enter_text))
        }else{
            viewModel.enterText(value)
        }
    }
}
