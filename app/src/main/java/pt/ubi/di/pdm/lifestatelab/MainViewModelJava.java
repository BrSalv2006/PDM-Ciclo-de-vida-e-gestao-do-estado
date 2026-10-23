package pt.ubi.di.pdm.lifestatelab;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

@SuppressWarnings("unused") // Remove os avisos de "never used" no Android Studio
public class MainViewModelJava extends ViewModel {
    private final SavedStateHandle savedStateHandle;
    private final MutableLiveData<Integer> liveDataCounter = new MutableLiveData<>(0);

    public MainViewModelJava(SavedStateHandle savedStateHandle) {
        this.savedStateHandle = savedStateHandle;
    }

    // Em Java, o SavedStateHandle expõe nativamente LiveData (em vez de StateFlow)
    public LiveData<String> getSavedText() {
        return savedStateHandle.getLiveData("USER_NOTE", "");
    }

    public void updateSavedText(String note) {
        savedStateHandle.set("USER_NOTE", note);
    }

    public LiveData<Integer> getLiveDataCounter() {
        return liveDataCounter;
    }

    public void incrementLiveData() {
        if (liveDataCounter.getValue() != null) {
            liveDataCounter.setValue(liveDataCounter.getValue() + 1);
        }
    }
}