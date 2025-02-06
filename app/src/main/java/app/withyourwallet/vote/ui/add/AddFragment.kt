package app.withyourwallet.vote.ui.add

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.ui.AppBarConfiguration
import app.withyourwallet.vote.ImprovedJsonObjectRequest
import app.withyourwallet.vote.databinding.FragmentAddBinding
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import org.json.JSONException
import org.json.JSONObject
import kotlin.concurrent.thread


class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null

    private lateinit var appBarConfiguration: AppBarConfiguration

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var subjectType: String = ""

    private var progressDialog: AlertDialog? = null

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

        _binding = FragmentAddBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.radioBrands.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
            {
                subjectType = "Brand"
            }
        }

        binding.radioRetailers.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
            {
                subjectType = "Retailer"
            }
        }

        binding.buttonSuggestSubjectSend.setOnClickListener {
            // Check that the name is valid.
            if (binding.suggestFormName.text.length < 2) {
                Snackbar.make(root,
                    "Name must contain at least 2 characters.",
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
                    binding.suggestFormName.text.toString())
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun send(emailAddress: String, userName: String, name: String) {
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

        val url = "https://api.withyourwallet.app/api/Subjects/Suggest"

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(activity)

        val postData = JSONObject()
        try {
            postData.put("emailAddress", emailAddress)
            postData.put("userName", userName)
            postData.put("name", name)
            postData.put("type", subjectType)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val request = ImprovedJsonObjectRequest(
            Request.Method.POST, url, postData,
            { _ ->
                activity?.runOnUiThread {
                    binding.radioGroupSubjectType.clearCheck()
                    binding.suggestFormName.setText(buildString { })

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
