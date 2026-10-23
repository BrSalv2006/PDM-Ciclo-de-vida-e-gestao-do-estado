package pt.ubi.di.pdm.lifestatelab

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import pt.ubi.di.pdm.lifestatelab.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private var volatileCounter: Int = 0
    private var bundleCounter: Int = 0

    fun logEvent(source: String, callbackName: String) {
        viewModel.addLog(source, callbackName)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logEvent("ACT", "onCreate()")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Compensa a barra de estado e o notch/recorte da câmara em modo horizontal
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            val density = resources.displayMetrics.density
            val extraMarginPx = (16 * density).toInt()

            view.setPadding(
                systemBars.left + extraMarginPx,
                systemBars.top + extraMarginPx,
                systemBars.right + extraMarginPx,
                systemBars.bottom + extraMarginPx
            )
            insets
        }

        if (savedInstanceState != null) {
            bundleCounter = savedInstanceState.getInt("KEY_BUNDLE", 0)
        }

        updateUI()

        // Recolhe o histórico de logs preservado no ViewModel
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.logsState.collect { logsText ->
                    binding.tvLogs.text = logsText
                    binding.scrollLogs.post {
                        binding.scrollLogs.fullScroll(android.view.View.FOCUS_DOWN)
                    }
                }
            }
        }

        // Sincroniza o botão com a pilha de Fragments
        supportFragmentManager.addOnBackStackChangedListener {
            updateButtonState()
        }
        updateButtonState()

        binding.btnIncrementAll.setOnClickListener {
            volatileCounter += 1
            bundleCounter += 1
            viewModel.vmCounter += 1
            updateUI()
        }

        binding.btnToggleFragment.setOnClickListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
            if (currentFragment == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, DetailFragment())
                    .addToBackStack("FRAGMENT_DETAIL").commit()
            } else {
                supportFragmentManager.popBackStack()
            }
        }

        binding.btnClearLogs.setOnClickListener {
            viewModel.clearLogs()
        }
    }

    private fun updateButtonState() {
        val fragmentAtivo = supportFragmentManager.findFragmentById(R.id.fragmentContainer) != null
        val stringRes = if (fragmentAtivo) {
            R.string.btn_close_fragment
        } else {
            R.string.btn_open_fragment
        }
        binding.btnToggleFragment.setText(stringRes)
    }

    private fun updateUI() {
        binding.tvVolatile.text = getString(R.string.volatile_counter_format, volatileCounter)
        binding.tvBundle.text = getString(R.string.bundle_counter_format, bundleCounter)
        binding.tvViewModel.text = getString(R.string.vm_counter_format, viewModel.vmCounter)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("KEY_BUNDLE", bundleCounter)
        logEvent("ACT", "onSaveInstanceState()")
    }

    override fun onStart() {
        super.onStart()
        logEvent("ACT", "onStart()")
    }

    override fun onResume() {
        super.onResume()
        logEvent("ACT", "onResume()")
    }

    override fun onPause() {
        logEvent("ACT", "onPause()")
        super.onPause()
    }

    override fun onStop() {
        logEvent("ACT", "onStop()")
        super.onStop()
    }

    override fun onDestroy() {
        logEvent("ACT", "onDestroy()")
        super.onDestroy()
    }
}