package app.withyourwallet.vote.android.ui.details

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.withyourwallet.vote.android.R
import app.withyourwallet.vote.android.databinding.FragmentDetailsBinding
import app.withyourwallet.vote.android.models.Score
import app.withyourwallet.vote.android.models.Subject
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import java.time.LocalDateTime
import kotlin.concurrent.thread

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var subjectId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        (activity as AppCompatActivity).supportActionBar?.show()

        val detailsViewModel =
            ViewModelProvider(this).get(DetailsViewModel::class.java)

        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        subjectId = arguments?.getInt("subjectId")

        val buttonSuggestScoreSend: Button = binding.buttonSuggestScore
        buttonSuggestScoreSend.setOnClickListener {
            // Navigate to the suggest score fragment
            val bundle = bundleOf("subjectId" to subjectId)
            findNavController().navigate(R.id.action_detailsFragment_to_suggestScoreFragment, bundle)
        }

        thread(start = true) {
            loadSubject()
            loadDiversityScores()
        }

        val textViewSubjectName: TextView = binding.textViewSubjectName
        textViewSubjectName.text = arguments?.getInt("subjectId").toString()

        val emptyScores = mutableListOf<Score>()

        val diversityScoreAdapter = ScoreAdapter({ score -> adapterOnClick(score) }, emptyScores)
        val diversityRecyclerView: RecyclerView = binding.diversityScoresRecyclerView
        diversityRecyclerView.layoutManager = LinearLayoutManager(activity)
        diversityRecyclerView.adapter = diversityScoreAdapter

        val environmentScoreAdapter = ScoreAdapter({ score -> adapterOnClick(score) }, emptyScores)
        val environmentRecyclerView: RecyclerView = binding.environmentalScoresRecyclerView
        environmentRecyclerView.layoutManager = LinearLayoutManager(activity)
        environmentRecyclerView.adapter = environmentScoreAdapter

        val unionScoreAdapter = ScoreAdapter({ score -> adapterOnClick(score) }, emptyScores)
        val unionRecyclerView: RecyclerView = binding.unionsScoresRecyclerView
        unionRecyclerView.layoutManager = LinearLayoutManager(activity)
        unionRecyclerView.adapter = unionScoreAdapter

        val politicsScoreAdapter = ScoreAdapter({ score -> adapterOnClick(score) }, emptyScores)
        val politicsRecyclerView: RecyclerView = binding.politicsScoresRecyclerView
        politicsRecyclerView.layoutManager = LinearLayoutManager(activity)
        politicsRecyclerView.adapter = politicsScoreAdapter

//        val textViewSubjectName: TextView = binding.textViewSubjectName
//        detailsViewModel.text.observe(viewLifecycleOwner) {
//            textViewSubjectName.text = it
//        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        (activity as AppCompatActivity).supportActionBar?.hide()
        _binding = null
    }

    private fun adapterOnClick(score: Score) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(score.sourceUrl))
        startActivity(browserIntent)
    }

    private fun loadSubject() {
        val url = "https://api.withyourwallet.app/api/Subjects/$subjectId"

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(activity)

        val request: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, url, null,
            { response ->
                try {
                    val subject = Subject()
                    subject.name = response.getString("name")
                    subject.id = response.getInt("id")
                    subject.diversityScore = response.getInt("diversityScore")
                    subject.environmentalScore = response.getInt("environmentalScore")
                    subject.unionsScore = response.getInt("unionScore")
                    subject.lobbyingScore = response.getInt("lobbyingScore")

                    activity?.runOnUiThread {
                        val textViewSubjectName: TextView = binding.textViewSubjectName
                        val imageViewDiversityScore: ImageView = binding.imageViewDiversityScore
                        val imageViewEnvironmentalScore: ImageView = binding.imageViewEnvironmentalScore
                        val imageViewUnionScore: ImageView = binding.imageViewUnionScore
                        val imageViewPoliticsScore: ImageView = binding.imageViewPoliticsScore

                        textViewSubjectName.text = subject.name

                        // Configure the score indicators.
                        configureImage(subject.diversityScore, imageViewDiversityScore)
                        configureImage(subject.environmentalScore, imageViewEnvironmentalScore)
                        configureImage(subject.unionsScore, imageViewUnionScore)
                        configureImage(subject.lobbyingScore, imageViewPoliticsScore)
                    }

                } catch (e: JSONException) {
                    print(e.toString())
//                    activity?.runOnUiThread {
//                        progressDialog?.dismiss()
//                        Log.w(TAG, "Could not understand time zone response")
//                        Log.w(TAG, response.toString())
//                        showDialog("Could not understand the response from the server, please try again.")
//                    }
                }
            }, { e ->
                print(e.toString())
//                activity?.runOnUiThread {
//                    val response = error.networkResponse
//                    when (response.statusCode) {
//                        else -> {
//                            progressDialog?.dismiss()
//                            showDialog("Could not reach the server, please try again.")
//                        }
//                    }
//                }
            }) {
            override fun getHeaders(): Map<String, String> {
                return HashMap()
            }
        }

        // Increase number of retries because we may be on a poor connection.
        request.retryPolicy = DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        // Add the request to the RequestQueue.
        queue.add(request)
    }

    private fun loadDiversityScores() {
        val url = "https://api.withyourwallet.app/api/Subjects/$subjectId/Scores"

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(activity)

        val request: JsonArrayRequest = object : JsonArrayRequest(Method.GET, url, null,
            { response ->
                try {
                    val diversityScores = mutableListOf<Score>()
                    val environmentalScores = mutableListOf<Score>()
                    val unionScores = mutableListOf<Score>()
                    val politicsScores = mutableListOf<Score>()

                    for (i in 0 until response.length()) {
                        val score = Score()
                        score.headline = response.getJSONObject(i).getString("headline")
                        score.id = response.getJSONObject(i).getInt("id")
                        score.number = response.getJSONObject(i).getInt("number")
                        score.sourceUrl = response.getJSONObject(i).getString("sourceUrl")
                        score.topic = response.getJSONObject(i).getString("topic")
                        score.createdAt = LocalDateTime.parse(response.getJSONObject(i).getString("createdAt"))
                        score.createdByUserName = response.getJSONObject(i).getJSONObject("createdByUser").getString("name")

                        when (score.topic) {
                            "Diversity" -> {
                                diversityScores.add(score)
                            }
                            "Environment" -> {
                                environmentalScores.add(score)
                            }
                            "Unions" -> {
                                unionScores.add(score)
                            }
                            "Politics" -> {
                                politicsScores.add(score)
                            }
                        }
                    }

                    activity?.runOnUiThread {
                        val diversityScoreAdapter = ScoreAdapter({ score -> adapterOnClick(score) }, diversityScores)
                        val diversityRecyclerView: RecyclerView = binding.diversityScoresRecyclerView
                        diversityRecyclerView.adapter = diversityScoreAdapter

                        val environmentScoreAdapter = ScoreAdapter({ score -> adapterOnClick(score) }, environmentalScores)
                        val environmentRecyclerView: RecyclerView = binding.environmentalScoresRecyclerView
                        environmentRecyclerView.adapter = environmentScoreAdapter

                        val unionScoreAdapter = ScoreAdapter({ score -> adapterOnClick(score) }, unionScores)
                        val unionRecyclerView: RecyclerView = binding.unionsScoresRecyclerView
                        unionRecyclerView.adapter = unionScoreAdapter

                        val politicsScoreAdapter = ScoreAdapter({ score -> adapterOnClick(score) }, politicsScores)
                        val politicsRecyclerView: RecyclerView = binding.politicsScoresRecyclerView
                        politicsRecyclerView.adapter = politicsScoreAdapter
                    }

                } catch (e: JSONException) {
                    print(e.toString())
//                    activity?.runOnUiThread {
//                        progressDialog?.dismiss()
//                        Log.w(TAG, "Could not understand time zone response")
//                        Log.w(TAG, response.toString())
//                        showDialog("Could not understand the response from the server, please try again.")
//                    }
                }
            }, { e ->
                print(e.toString())
//                activity?.runOnUiThread {
//                    val response = error.networkResponse
//                    when (response.statusCode) {
//                        else -> {
//                            progressDialog?.dismiss()
//                            showDialog("Could not reach the server, please try again.")
//                        }
//                    }
//                }
            }) {
            override fun getHeaders(): Map<String, String> {
                return HashMap()
            }
        }

        // Increase number of retries because we may be on a poor connection.
        request.retryPolicy = DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        // Add the request to the RequestQueue.
        queue.add(request)
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
