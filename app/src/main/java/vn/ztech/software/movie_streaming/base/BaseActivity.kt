package vn.ztech.software.movie_streaming.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

typealias ActivityInflate<T> = (LayoutInflater) -> T
abstract class BaseActivity<VB: ViewBinding>(private val inflate: ActivityInflate<VB>):
    AppCompatActivity() {

    var binding: VB? = null
    abstract val viewModel: BaseViewModel

    protected abstract fun initialize()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate.invoke(layoutInflater)
        setContentView(binding?.root)
        initialize()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
