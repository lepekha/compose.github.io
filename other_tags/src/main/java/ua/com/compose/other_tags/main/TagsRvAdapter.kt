package ua.com.compose.other_tags.main

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.module_other_tags_element_tag.view.*
import ua.com.compose.extension.clipboardCopy
import ua.com.compose.other_tags.R
import java.util.regex.Matcher
import java.util.regex.Pattern


class TagsRvAdapter(val tags: MutableList<String>, val filterString: StringBuffer) : RecyclerView.Adapter<ViewHolder>() {

    override fun getItemCount(): Int {
        return tags.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.module_other_tags_element_tag, parent, false)).apply {
            this.btnCopy.setOnClickListener {
                parent.context.clipboardCopy(text = txtTags.text.toString())
                Toast.makeText(parent.context, parent.context.getString(R.string.module_other_tags_fragment_text_copied), Toast.LENGTH_SHORT).show();
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val filterText = filterString.toString()
        val color = ContextCompat.getColor(holder.txtTags.context, R.color.fragment_tags_text_highlight)
        if(filterText.isNotEmpty() and filterText.isNotBlank()){
            val sb = SpannableStringBuilder(tags[position])
            val p: Pattern = Pattern.compile(filterString.toString(), Pattern.CASE_INSENSITIVE)
            val m: Matcher = p.matcher(tags[position])
            while (m.find()) {
                sb.setSpan(BackgroundColorSpan(color), m.start(), m.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }
            holder.txtTags.text = sb
        }else{
            holder.txtTags.text = tags[position]
        }
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val txtTags: TextView = view.txtTags
    val btnCopy: ImageButton = view.btnCopy
}
