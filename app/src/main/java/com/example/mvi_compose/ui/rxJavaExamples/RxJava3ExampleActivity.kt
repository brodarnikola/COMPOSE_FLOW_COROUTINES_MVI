package com.example.mvi_compose.ui.rxJavaExamples


import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.mvi_compose.R
import com.example.mvi_compose.databinding.Rxjava2TutorialBinding
import com.example.mvi_compose.movies.network.GithubApi
import com.example.mvi_compose.movies.network.data.github.GithubResponseApi
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
 import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Predicate
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.random.Random


private const val UPDATE_PERIOD = 10000L
private const val RX_JAVA_TAG = "RxJavaTag"

private lateinit var viewModel: RxJava2ViewModel
//private val viewModel : RxJava2ViewModel by viewModels()

private var automaticIncreaseNumberByOne: Job? = null

var githubReposCompositeDisposable: CompositeDisposable? = null

val BASE_URL = "https://api.github.com/"

private var adapter: ReposRxJava2FlatMapAdapter? = null


@AndroidEntryPoint
class RxJava3ExampleActivity : AppCompatActivity() { // AppCompatActivity() {

    var binding: Rxjava2TutorialBinding? = null

    // Here on this screen, can I please explain what is happening, how rxjava2 is working and functining with observable, observer and operators
    // https://github.com/amitshekhariitbhu/RxJava2-Android-Samples/blob/master/app/src/main/java/com/rxjava2/android/samples/ui/networking/NetworkingActivity.java

    @RequiresApi(Build.VERSION_CODES.S)
    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Assign variable
        binding = DataBindingUtil.setContentView(this, R.layout.rxjava2_tutorial)

        viewModel = ViewModelProvider(this)[RxJava2ViewModel::class.java]


        // Create the observer which updates the UI.
        val nameObserver = androidx.lifecycle.Observer<Int> { currentNumber ->
            // Update the UI, in this case, a TextView.
            binding?.tvNumberIncreaseAutomatically?.text = "" + currentNumber
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.incrementNumberAutomaticallyByOne.observe(this, nameObserver)

//        viewModel.incrementNumberAutomaticallyByOne.observe(lifecycleScope, { currentNumber -> // Observer { currentNumber ->
//            binding?.tvNumberIncreaseAutomatically?.text = "" + currentNumber
//        })
    }

//    @RequiresApi(Build.VERSION_CODES.S)
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState)
//
//        binding = Rxjava2TutorialBinding.inflate(inflater, container, false)
//
//        viewModel.incrementNumberAutomaticallyByOne.observe(viewLifecycleOwner, { currentNumber -> // Observer { currentNumber ->
//            binding?.tvNumberIncreaseAutomatically?.text = "" + currentNumber
//        })
//
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        viewModel = ViewModelProvider(this)[RxJava2ViewModel::class.java]
//    }

    private fun initRecyclerView() {
        adapter = ReposRxJava2FlatMapAdapter()
        binding?.listReposFlatMap?.setAdapter(adapter)
    }

    override fun onResume() {
        super.onResume()

        rxJava2Tutorials()

        rxJava2FlatMapExample()

        automaticIncreaseNumberByOne?.cancel()
        automaticIncreaseNumberByOne = lifecycleScope.launch {
            while (true) {
                try {
                    viewModel.incrementAutomaticallyByOne()
                } catch (ex: Exception) {
                    Log.v(RX_JAVA_TAG,"Periodic remote-update failed...", ex)
                }
                delay(UPDATE_PERIOD)
            }
        }
    }

    private fun rxJava2FlatMapExample() {
        initRecyclerView()
        getPostObservable()
            .flatMap { posts ->
                getCommentsObservable(posts)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Post> {


                override fun onComplete() {}


                override fun onSubscribe(d: Disposable) {
                    githubReposCompositeDisposable?.add(d)
                }

                override fun onNext(post: Post) {
                    adapter?.updatePost(post)
                }

                override fun onError(e: Throwable) {
                    Log.e(RX_JAVA_TAG, "onError received: ", e)
                }
            })
    }

    private fun getPostObservable() : Observable<Post> {

        val resultPost = setupRetrofitFlatMap()
            .getPosts()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { posts: List<Post> ->
                // Update your adapter with the received list of posts
                adapter?.setPosts(posts.toMutableList())

                // Transform the list of posts to an Observable emitting individual posts
                Observable.fromIterable(posts)
                    .subscribeOn(Schedulers.io())
            }
            .onErrorReturn { error ->
                Log.e(RX_JAVA_TAG, "onError received: $error")
                Post() // Return a default Post in case of error
            }
        return resultPost
    }

    private fun getCommentsObservable(post: Post) : Observable<Post> {
        val resultPostComments = setupRetrofitFlatMap()
            .getComments( post.id )
            .map { comments ->

                val delay: Int = (Random.nextInt(3) + 1) * 1000 // sleep thread for x ms

                try {
                    Thread.sleep(delay.toLong())
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt() // restore interrupted status
                } catch (exception: Exception) {
                    Log.e(RX_JAVA_TAG, "onError received: ${exception}")
                }
                Log.d(
                    RX_JAVA_TAG,
                    "apply: sleeping thread " + Thread.currentThread()
                        .name + " for " + delay.toString() + "ms"
                )

                val postWithComments = post.copy(comments = comments)
//                post.comments = comments
                postWithComments

            }
            .subscribeOn(Schedulers.io())
            .onErrorReturn { error ->
                Log.e(RX_JAVA_TAG, "onError received: ${error}")
                Post()
            }

        return resultPostComments
    }

    private fun setupRetrofitFlatMap(): GithubApi {
        return Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
             .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build().create(GithubApi::class.java)
    }

    private fun rxJava2Tutorials() {

        setupCompositeDisposable()

        simpleObservablesAndObservers()
    }

    private fun setupCompositeDisposable() {

        githubReposCompositeDisposable = CompositeDisposable()
        githubReposCompositeDisposable?.addAll(
            searchGithubRepos(3),
            searchGithubRepos(3)
        )
    }

    private fun searchGithubRepos( sizeOfGithubRepos: Int): Disposable {
        // this is a single example of rxjava2 for github repositories
        return  viewModel.getGithubRepositories("java", 1, sizeOfGithubRepos)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn { error ->
                Log.e(RX_JAVA_TAG, "onError received: ${error}")
                GithubResponseApi(0, false, listOf())
            }
            .subscribe(this::handleResponse, this::onError)
    }

    private fun onError(error: Throwable) {
        Log.e(RX_JAVA_TAG, "onError received: ${error}")
    }

    private fun handleResponse(GithubResponseApi: GithubResponseApi) {

        Log.d(RX_JAVA_TAG, "Size of composite disposable stream and rest api from github: " + GithubResponseApi.items.size)
        var reposResult = ""
        GithubResponseApi.items.forEach { repos ->
            reposResult += "Name of repository: " + repos.name + "\n"
        }
        val currectText = binding?.tvCompositeDisposableValue?.text.toString()
        binding?.tvCompositeDisposableValue?.setText(reposResult + currectText )
    }

    private fun simpleObservablesAndObservers() {

        val intervalObservable = Observable
            .interval(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .takeWhile(object : Predicate<Long> {
                // stop the process if more than 5 seconds passes
                @Throws(java.lang.Exception::class)
                override fun test(longNumber: Long): Boolean {
                    return longNumber <= 5
                }
            })
            .observeOn(AndroidSchedulers.mainThread())

        intervalObservable.subscribe(object : Observer<Long> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(aLong: Long) {
                Log.d(RX_JAVA_TAG, "AAAA onNext: interval: $aLong")
            }

            override fun onComplete() {}

            override fun onError(e: Throwable) {
                Log.e(RX_JAVA_TAG, "error: $e")
            }
        })

        val animalsObservable =
            Observable.just("Ant", "Bee", "Cat", "Dog", "Fox")

        val animalObserver= getAnimalsObserver()

        animalsObservable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .map { name -> name.uppercase() }
            .subscribe(animalObserver)


        val d = Observable.just(1, 2, 3)
            .map { i: Int -> i * i }
            .map { i: Int -> i * i }
            .filter { i: Int -> i > 10 }
            .subscribe { x: Int? ->
                Log.d(RX_JAVA_TAG, "Numbers greather than 10 are: " + x)
                println(x) }
    }

    private fun getAnimalsObserver(): Observer<String> {
        return object : Observer<String> {

            override fun onNext(s: String) {
                Log.d(RX_JAVA_TAG, "Name: $s")
            }

            override fun onError(e: Throwable) {
                Log.e(
                    RX_JAVA_TAG,
                    "onError: " + e.message
                )
            }

            override fun onComplete() {
                Log.d(
                    RX_JAVA_TAG,
                    "All items are emitted!"
                )
            }
            override fun onSubscribe(d: Disposable) {
                Log.d(RX_JAVA_TAG, "onSubscribe")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        githubReposCompositeDisposable?.clear()
    }

    override fun onStop() {
        super.onStop()
        githubReposCompositeDisposable?.apply {
            if( !isDisposed ) {
                dispose()
                githubReposCompositeDisposable = null
            }
        }
    }



}