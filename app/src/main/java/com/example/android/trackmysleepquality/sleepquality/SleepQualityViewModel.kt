

package com.example.android.trackmysleepquality.sleepquality

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import kotlinx.coroutines.*

class SleepQualityViewModel (
        private val sleepNightKey: Long = 0L,
        val database: SleepDatabaseDao) : ViewModel(){

    //viewModelJob allows us to cancel all coroutines started by this ViewModel.
    private val viewModelJob = Job()

    /**
     * Variable that tells the fragment whether it should navigate to [SleepTrackerFragment].
     *
     * This is `private` because we don't want to expose the ability to set [MutableLiveData] to
     * the [Fragment]
     */

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

   // When true immediately navigate back to the [SleepTrackerFragment]
    private val _navigateToSleepTracker = MutableLiveData<Boolean?>()

    val navigateToSleepTracker: LiveData<Boolean?>
    get() = _navigateToSleepTracker

    //Call this immediately after navigating to [SleepTrackerFragment]
    fun doneNavigating(){
        _navigateToSleepTracker.value = null
    }

    /**
     * Sets the sleep quality and updates the database.
     *
     * Then navigates back to the SleepTrackerFragment.
     */

    fun onSetSleepQuality(quality: Int){
        uiScope.launch {
            withContext(Dispatchers.IO){
                val tonight = database.get(sleepNightKey) ?: return@withContext
                tonight.sleepQuality = quality
                database.update(tonight)
            }
            // Setting this state variable to true will alert the observer and trigger navigation.
            _navigateToSleepTracker.value = true
        }
    }

    /**
     * Cancels all coroutines when the ViewModel is cleared, to cleanup any pending work.
     *
     * onCleared() gets called when the ViewModel is destroyed.
     */

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}


