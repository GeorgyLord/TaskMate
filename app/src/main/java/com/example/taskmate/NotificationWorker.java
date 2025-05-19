package com.example.taskmate;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NotificationWorker extends Worker {

    public NotificationWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params
    ) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Получаем данные из входных параметров
        String title = getInputData().getString("title");
        String message = getInputData().getString("message");

        // Показываем уведомление
        NotificationUtils.showNotification(getApplicationContext(), title, message);

        // Возвращаем результат:
        // Result.success() - успех
        // Result.failure() - ошибка
        // Result.retry() - повторить попытку
        return Result.success();
    }
}