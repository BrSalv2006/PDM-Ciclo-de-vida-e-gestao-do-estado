package pt.ubi.di.pdm.lifestatelab

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import pt.ubi.di.pdm.lifestatelab.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private fun log(callback: String) {
        val parentActivity = activity as? MainActivity
        parentActivity?.logEvent("FRG", callback)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        log("onAttach()")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log("onCreate()")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        log("onCreateView()")
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log("onViewCreated()")

        binding.tvFragmentStatus.setText(R.string.fragment_status_active)

        // Atualizar o SavedStateHandle em tempo real
        binding.etNote.doAfterTextChanged { text ->
            viewModel.updateSavedText(text.toString())
        }

        // Recuperar texto salvo pelo SavedStateHandle via Flow
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.savedText.collect { saved ->
                    if (binding.etNote.text.toString() != saved) {
                        binding.etNote.setText(saved)
                    }
                }
            }
        }

        // Subscrição segura de StateFlow
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.flowCounter.collect { count ->
                    binding.tvFlowCount.text = getString(R.string.flow_counter_format, count)
                }
            }
        }

        // Subscrição nativa de LiveData
        viewModel.liveDataCounter.observe(viewLifecycleOwner) { count ->
            binding.tvLiveDataCount.text = getString(R.string.livedata_counter_format, count)
        }

        binding.btnFlow.setOnClickListener {
            viewModel.incrementFlow()
        }

        binding.btnLiveData.setOnClickListener {
            viewModel.incrementLiveData()
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        log("onViewStateRestored()")
    }

    override fun onStart() {
        super.onStart()
        log("onStart()")
    }

    override fun onResume() {
        super.onResume()
        log("onResume()")
    }

    override fun onPause() {
        log("onPause()")
        super.onPause()
    }

    override fun onStop() {
        log("onStop()")
        super.onStop()
    }

    override fun onDestroyView() {
        log("onDestroyView()")
        _binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        log("onDestroy()")
        super.onDestroy()
    }

    override fun onDetach() {
        log("onDetach()")
        super.onDetach()
    }
}