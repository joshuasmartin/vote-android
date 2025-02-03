package app.withyourwallet.vote.android.ui.add

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import app.withyourwallet.vote.android.ImprovedJsonObjectRequest
import app.withyourwallet.vote.android.databinding.FragmentSuggestScoreBinding
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import org.json.JSONException
import org.json.JSONObject
import kotlin.concurrent.thread

class SuggestScoreFragment : Fragment() {

    private var _binding: FragmentSuggestScoreBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var subjectId: Int? = null

    private var number: Int = 1

    private var topic: String = ""

    private var progressDialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSuggestScoreBinding.inflate(inflater, container, false)
        val root: View = binding.root

        subjectId = arguments?.getInt("subjectId")

        // Configure the DEI topic radio button.
        binding.radioTopicDei.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
            {
                topic = "DEI"
            }
        }

        // Configure the environment topic radio button.
        binding.radioTopicEnvironment.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
            {
                topic = "Environment"
            }
        }

        // Configure the unions topic radio button.
        binding.radioTopicUnions.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
            {
                topic = "Unions"
            }
        }

        // Configure the politics topic radio button.
        binding.radioTopicPolitics.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
            {
                topic = "Politics"
            }
        }

        // Configure the negative number radio button.
        binding.radioNumberNegative.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
            {
                number = 1
            }
        }

        // Configure the positive number radio button.
        binding.radioNumberPositive.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
            {
                number = 3
            }
        }

        binding.buttonSuggestScoreSend.setOnClickListener {
            // Check that the headline is valid.
            if (binding.suggestScoreFormHeadline.text.length < 7) {
                Snackbar.make(root,
                    "Headline must contain at least 7 characters.",
                    Snackbar.LENGTH_SHORT)
                    .show()

                return@setOnClickListener
            }

            // Check that the source is valid.
            if (binding.suggestScoreFormSource.text.length < 7) {
                Snackbar.make(root,
                    "Source URL must contain at least 7 characters.",
                    Snackbar.LENGTH_SHORT)
                    .show()

                return@setOnClickListener
            }

            // Check that the user name is valid.
            if (binding.suggestFormUserName.text.length < 4) {
                Snackbar.make(root,
                    "Your name must contain at least 4 characters.",
                    Snackbar.LENGTH_SHORT)
                    .show()

                return@setOnClickListener
            }

            // Check that the email address is valid.
            val matches = android.util.Patterns.EMAIL_ADDRESS.matcher(binding.suggestFormEmailAddress.text).matches()
            if (!matches) {
                Snackbar.make(root,
                    "Must provide a valid Email address.",
                    Snackbar.LENGTH_SHORT)
                    .show()

                return@setOnClickListener
            }

            thread(start = true) {
                send(
                    binding.suggestFormEmailAddress.text.toString(),
                    binding.suggestFormUserName.text.toString(),
                    binding.suggestScoreFormHeadline.text.toString(),
                    binding.suggestScoreFormSource.text.toString())
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun send(emailAddress: String, userName: String, headline: String, sourceUrl: String) {
        activity?.runOnUiThread {

            // Display a progress dialog to disable the interface.
            val builder = AlertDialog.Builder(activity)
            builder.setMessage("Working")
            builder.setCancelable(false)
            progressDialog = builder.create()
            progressDialog?.setCancelable(false)
            progressDialog?.setCanceledOnTouchOutside(false)
            progressDialog?.show()
        }

        val url = "https://api.withyourwallet.app/api/Subjects/$subjectId/Scores"

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(activity)

        val postData = JSONObject()
        try {
            postData.put("emailAddress", emailAddress)
            postData.put("userName", userName)
            postData.put("number", number)
            postData.put("topic", topic)
            postData.put("headline", headline)
            postData.put("sourceUrl", sourceUrl)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val request = ImprovedJsonObjectRequest(
            Request.Method.POST, url, postData,
            { _ ->
                activity?.runOnUiThread {
                    binding.radioGroupScoreTopic.clearCheck()
                    binding.radioGroupScoreNumber.clearCheck()
                    binding.suggestScoreFormHeadline.setText(buildString { })
                    binding.suggestScoreFormSource.setText(buildString { })

                    progressDialog?.dismiss()

                    Toast.makeText(
                        activity,
                        "Thank you for your suggestion! We will review and add it to the database.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            { _ ->
                activity?.runOnUiThread {
                    progressDialog?.dismiss()

                    Toast.makeText(
                        activity,
                        "Could not reach the server, please try again.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })

        // Increase number of retries because we may be on a poor connection.
        request.retryPolicy = DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        // Add the request to the RequestQueue.
        queue.add(request)
    }
}
