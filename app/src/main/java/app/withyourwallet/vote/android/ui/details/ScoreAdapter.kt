package app.withyourwallet.vote.android.ui.details

import android.graphics.Color
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.withyourwallet.vote.android.R
import app.withyourwallet.vote.android.models.Score
import java.time.format.DateTimeFormatter

class ScoreAdapter(private val onClick: (Score) -> Unit,
                   private val dataSet: MutableList<Score>) :
    ListAdapter<Score, ScoreAdapter.ViewHolder>(ScoreDiffCallback) {

    class ViewHolder(view: View, val onClick: (Score) -> Unit) :
        RecyclerView.ViewHolder(view) {
        private val textViewScoreNumber: TextView = view.findViewById(R.id.text_view_score_number)
        private val textViewScoreHeadline: TextView = view.findViewById(R.id.text_view_score_headline)
        private val textViewScoreByline: TextView = view.findViewById(R.id.text_view_score_byline)
        private var currentScore: Score? = null

        init {
            view.setOnClickListener {
                currentScore?.let {
                    onClick(it)
                }
            }
        }

        fun bind(score: Score) {
            currentScore = score

            when (currentScore?.number) {
                3 -> {
                    val color = Color.parseColor("#4CAF50")
                    textViewScoreNumber.text =
                        itemView.context.getString(R.string.scores_title_positive)
                    textViewScoreNumber.setTextColor(color)
                }
                2 -> {
                    val color = Color.parseColor("#4D4D4D")
                    textViewScoreNumber.text =
                        itemView.context.getString(R.string.scores_title_neutral)
                    textViewScoreNumber.setTextColor(color)
                }
                1 -> {
                    val color = Color.parseColor("#F44336")
                    textViewScoreNumber.text =
                        itemView.context.getString(R.string.scores_title_negative)
                    textViewScoreNumber.setTextColor(color)
                }
            }

            val link = String.format("<a href=\"%S\">%S</a>",
                currentScore?.sourceUrl,
                currentScore?.headline)
            textViewScoreHeadline.text = Html.fromHtml(link, HtmlCompat.FROM_HTML_MODE_LEGACY)

            val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy h:m a")
            val byline = String.format("Reported by %S at %S",
                currentScore?.createdByUserName,
                currentScore?.createdAt?.format(formatter))
            textViewScoreByline.text = byline
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.row_item_score, viewGroup, false)

        return ViewHolder(view, onClick)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val score = dataSet[position]

        viewHolder.bind(score)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}

object ScoreDiffCallback : DiffUtil.ItemCallback<Score>() {
    override fun areItemsTheSame(oldItem: Score, newItem: Score): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Score, newItem: Score): Boolean {
        return oldItem.id == newItem.id
    }
}
