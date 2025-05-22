package edu.deakin.s600152989.sit305.a91p;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import java.io.File;

public class CleanUp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        deleteCorruptedSqliteCache(this);
    }

    private void deleteCorruptedSqliteCache(Context context) {
        File dbDir = new File(context.getDatabasePath("dummy").getParent());
        for (File file : dbDir.listFiles()) {
            String name = file.getName();
            if (name.contains("tile") || name.contains("map") || name.endsWith(".db")) {
                Log.d("DB_CLEANUP", "Deleting: " + name);
                file.delete();
            }
        }
    }
}
