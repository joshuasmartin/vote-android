package app.withyourwallet.vote.ui.brands

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.withyourwallet.vote.R
import app.withyourwallet.vote.models.Subject


class CustomAdapter(private val onClick: (Subject) -> Unit,
                    private val dataSet: Array<Subject?>
) :
    ListAdapter<Subject, CustomAdapter.ViewHolder>(SubjectDiffCallback) {
//    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    class ViewHolder(view: View, val onClick: (Subject) -> Unit) :
        RecyclerView.ViewHolder(view) {
        private val textView: TextView = view.findViewById(R.id.textView)
        private val imageViewDiversityScore: ImageView = view.findViewById(R.id.imageViewDiversityScore)
        private val imageViewEnvironmentalScore: ImageView = view.findViewById(R.id.imageViewEnvironmentalScore)
        private val imageViewUnionScore: ImageView = view.findViewById(R.id.imageViewUnionScore)
        private val imageViewPoliticsScore: ImageView = view.findViewById(R.id.imageViewPoliticsScore)
        private var currentSubject: Subject? = null

        init {
            view.setOnClickListener {
                currentSubject?.let {
                    onClick(it)
                }
            }
        }

        fun bind(subject: Subject) {
            currentSubject = subject

            textView.text = currentSubject!!.name

            // Configure the score indicators.
            configureImage(currentSubject?.diversityScore, imageViewDiversityScore)
            configureImage(currentSubject?.environmentalScore, imageViewEnvironmentalScore)
            configureImage(currentSubject?.unionsScore, imageViewUnionScore)
            configureImage(currentSubject?.lobbyingScore, imageViewPoliticsScore)
        }

        private fun configureImage(score: Int?, imageView: ImageView) {
            when (score) {
                3 -> {
                    val color = Color.parseColor("#4CAF50")
                    imageView.setColorFilter(color)
                    imageView.setImageResource(R.drawable.verified_user_24px)
                }
                2 -> {
                    val color = Color.parseColor("#FF9800")
                    imageView.setColorFilter(color)
                    imageView.setImageResource(R.drawable.gpp_maybe_24px)
                }
                1 -> {
                    val color = Color.parseColor("#F44336")
                    imageView.setColorFilter(color)
                    imageView.setImageResource(R.drawable.gpp_bad_24px)
                }
                else -> {
                    val color = Color.parseColor("#000000")
                    imageView.setColorFilter(color)
                    imageView.setImageResource(R.drawable.shield_question_24px)
                }
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.text_row_item, viewGroup, false)

        return ViewHolder(view, onClick)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val subject = dataSet[position]

        viewHolder.bind(subject!!)

//        viewHolder.itemView.setOnClickListener {
//            viewHolder.item.findNavController().navigate(
//                R.id.action_leaderboard_to_userProfile,
//                bundle)
//        }

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
//        viewHolder.textView.text = item.name
//        viewHolder.textViewDiversityScore.text = dataSet[position].diversityScore.toString()
//        viewHolder.textViewEnvironmentalScore.text = dataSet[position].environmentalScore.toString()
//        viewHolder.textViewUnionsScore.text = dataSet[position].unionsScore.toString()
//        viewHolder.textViewLobbyingScore.text = dataSet[position].lobbyingScore.toString()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}

object SubjectDiffCallback : DiffUtil.ItemCallback<Subject>() {
    override fun areItemsTheSame(oldItem: Subject, newItem: Subject): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Subject, newItem: Subject): Boolean {
        return oldItem.id == newItem.id
    }
}
