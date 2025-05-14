package edu.deakin.s600152989.sit305.a71p;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class LostFoundViewModel extends AndroidViewModel {

    private LostFoundRepository repository;
    private LiveData<List<LostFoundItem>> allItems;

    public LostFoundViewModel(Application application) {
        super(application);
        repository = new LostFoundRepository(application);
        allItems = repository.getAllItems();
    }

    public LiveData<List<LostFoundItem>> getAllItems() {
        return allItems;
    }

    public void insert(LostFoundItem item) {
        repository.insert(item);
    }

    public void delete(LostFoundItem item) {
        repository.delete(item);
    }
}
