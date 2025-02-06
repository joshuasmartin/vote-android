package app.withyourwallet.vote.ui.retailers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.withyourwallet.vote.R
import app.withyourwallet.vote.databinding.FragmentRetailersBinding
import app.withyourwallet.vote.models.Subject
import app.withyourwallet.vote.ui.brands.CustomAdapter
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import kotlin.concurrent.thread

class RetailersFragment : Fragment() {

    private var _binding: FragmentRetailersBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: RetailersViewModel by viewModels({requireParentFragment()})

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        (activity as AppCompatActivity).supportActionBar?.hide()
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        (activity as AppCompatActivity).supportActionBar?.hide()

        _binding = FragmentRetailersBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val emptySubjects = arrayOfNulls<Subject>(0)
        val customAdapter = CustomAdapter({ subject -> adapterOnClick(subject) }, emptySubjects)

        val recyclerView: RecyclerView = binding.myRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = customAdapter

        thread(start = true) {
            loadRetailers()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun adapterOnClick(subject: Subject) {
        val bundle = bundleOf("subjectId" to subject.id)
        findNavController().navigate(R.id.action_retailersFragment_to_detailsFragment, bundle)
    }

    private fun loadRetailers() {
        val url = "https://api.withyourwallet.app/api/Subjects/Retailers"

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(activity)

        val request: JsonArrayRequest = object : JsonArrayRequest(
            Method.GET, url, null,
            { response ->
                try {
                    val subjects = arrayOfNulls<Subject>(response.length())
                    for (i in 0 until response.length()) {
                        val subject = Subject()
                        subject.name = response.getJSONObject(i).getString("name")
                        subject.id = response.getJSONObject(i).getInt("id")
                        subject.diversityScore = response.getJSONObject(i).getInt("diversityScore")
                        subject.environmentalScore = response.getJSONObject(i).getInt("environmentalScore")
                        subject.unionsScore = response.getJSONObject(i).getInt("unionScore")
                        subject.lobbyingScore = response.getJSONObject(i).getInt("lobbyingScore")
                        subjects[i] = subject
                    }

                    activity?.runOnUiThread {
                        val customAdapter = CustomAdapter({ subject -> adapterOnClick(subject) }, subjects)

                        val recyclerView: RecyclerView = binding.myRecyclerView
                        recyclerView.adapter = customAdapter
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
}
