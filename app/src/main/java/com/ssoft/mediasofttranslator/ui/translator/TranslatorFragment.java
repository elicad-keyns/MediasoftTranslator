package com.ssoft.mediasofttranslator.ui.translator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.SparseArray;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ssoft.mediasofttranslator.MainActivity;
import com.ssoft.mediasofttranslator.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class TranslatorFragment extends Fragment {

    final String                SAVED_HISTORY = "saved_history";
    private ArrayList<String>    translateHistoryList;

    private static final int    REQUEST_CODE_SPEECH_INPUT = 1;
    private static final int    REQUEST_CODE_CAMERA_PHOTO = 2;

    private TranslatorViewModel translatorViewModel;

    private static EditText     et_input;
    private static EditText     et_output;
    private ImageButton         ib_translate;
    private ImageButton         ib_share;
    private ImageButton         ib_change;
    private ImageButton         ib_voice_input;
    private ImageButton         ib_photo_input;
    public  ImageButton         ib_history;
    private Spinner             s_input;
    private Spinner             s_output;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        translatorViewModel =
                ViewModelProviders.of(this).get(TranslatorViewModel.class);
        View view = inflater.inflate(R.layout.fragment_translator, container, false);

        translateHistoryList = new ArrayList<>();

        /*
        Создаем адаптер (Знаю можно было и без адаптера,
        но нам нужно стилизовать текст в спиннерах,
        чтобы уменьшить текст поэтому делаем через адаптер)
        */
        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(
                view.getContext(),
                R.array.languages,
                R.layout.spinner_item
        );

        // Ищем все нужные элементы (кнопки и т.д)
        et_input = view.findViewById(R.id.et_input);
        et_output = view.findViewById(R.id.et_output);
        ib_translate = view.findViewById(R.id.ib_translate);
        ib_share = view.findViewById(R.id.ib_share);
        ib_change = view.findViewById(R.id.ib_change);
        ib_voice_input = view.findViewById(R.id.ib_voice_input);
        ib_photo_input = view.findViewById(R.id.ib_photo_input);
        ib_history = view.findViewById(R.id.ib_history);
        s_input = view.findViewById(R.id.s_input);
        s_output = view.findViewById(R.id.s_output);

        // Сетаем адаптер в спиннеры
        s_input.setAdapter(arrayAdapter);
        s_output.setAdapter(arrayAdapter);

        // Обработчик кнопки перевода (которая со стрелочками)
        ib_translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.getTranslate(et_input.getText().toString(), getLangCode(s_input), getLangCode(s_output));
                translateHistoryList.add(et_input.getText().toString());
                saveHistory();
            }
        });

        // Обработчик кнопки "Поделиться" (С одной стрелочкой)
        ib_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareBody = et_input.getText() + " -> " + et_output.getText();
                String shareSub = "Поделиться";
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(shareIntent, "Поделиться"));
            }
        });

        // Обработчик кнопки смены языков (сверху между языками)
        ib_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int inputPosition = s_input.getSelectedItemPosition();
                s_input.setSelection(s_output.getSelectedItemPosition());
                s_output.setSelection(inputPosition);
            }
        });

        // Кнопка голосового ввода (С иконкой микрофона)
        ib_voice_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Speak();
            }
        });

        // Кнопка ввода текста с картинки (С иконкой объектива)
        ib_photo_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImage();
            }
        });

        // Кнопка для показа истории запросов
        ib_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadHistory();
                getHistory();
            }
        });

        return view;
    }

    // Вспомогающий меод чтобы сетить текст в поле вывода перевода,
    // нужно чтобы не юзать поле et_output из MainActivity
    public static void setOutput(String output) {
        et_output.setText(output);
    }

    public static String getInputOutputText(){
        return et_input.getText().toString() + " -> " + et_output.getText().toString();
    }

    // Метод для получения кода языка
    private String getLangCode(Spinner spinner) {
        switch (spinner.getSelectedItem().toString()) {
            case "Азербайджанский":
                return "az";
            case "Албанский":
                return "sq";
            case "Амхарский":
                return "am";
            case "Английский":
                return "en";
            case "Арабский":
                return "ar";
            case "Армянский":
                return "hy";
            case "Африкаанс":
                return "af";
            case "Баскский":
                return "eu";
            case "Башкирский":
                return "ba";
            case "Белорусский":
                return "be";
            case "Бенгальский":
                return "bn";
            case "Бирманский":
                return "my";
            case "Болгарский":
                return "bg";
            case "Боснийский":
                return "bs";
            case "Вваллийский":
                return "cy";
            case "Венгерский":
                return "hu";
            case "Вьетнамский":
                return "vi";
            case "Гаитянский (креольский)":
                return "ht";
            case "Галисийский":
                return "gl";
            case "Голландский":
                return "nl";
            case "Горномарийский":
                return "mrj";
            case "Греческий":
                return "el";
            case "Грузинский":
                return "ka";
            case "Гуджарати":
                return "gu";
            case "Датский":
                return "da";
            case "Иврит":
                return "he";
            case "Идиш":
                return "yi";
            case "Индонезийский":
                return "id";
            case "Ирландский":
                return "ga";
            case "Итальянский":
                return "it";
            case "Исландский":
                return "is";
            case "Испанский":
                return "es";
            case "Казахский":
                return "kk";
            case "Каннада":
                return "kn";
            case "Каталанский":
                return "ca";
            case "Киргизский":
                return "ky";
            case "Китайский":
                return "zh";
            case "Корейский":
                return "ko";
            case "Коса":
                return "xh";
            case "Кхмерский":
                return "km";
            case "Лаосский":
                return "lo";
            case "Латынь":
                return "la";
            case "Латышский":
                return "lv";
            case "Литовский":
                return "lt";
            case "Люксембургский":
                return "lb";
            case "Малагасийский":
                return "mg";
            case "Малайский":
                return "ms";
            case "Малаялам":
                return "ml";
            case "Мальтийский":
                return "mt";
            case "Македонский":
                return "mk";
            case "Маори":
                return "mi";
            case "Маратхи":
                return "mr";
            case "Марийский":
                return "mhr";
            case "Монгольский":
                return "mn";
            case "Немецкий":
                return "de";
            case "Непальский":
                return "ne";
            case "Норвежский":
                return "no";
            case "Панджаби":
                return "pa";
            case "Папьяменто":
                return "pap";
            case "Персидский":
                return "fa";
            case "Польский":
                return "pl";
            case "Португальский":
                return "pt";
            case "Румынский":
                return "ro";
            case "Русский":
                return "ru";
            case "Себуанский":
                return "ceb";
            case "Сербский":
                return "sr";
            case "Сингальский":
                return "si";
            case "Словацкий":
                return "sk";
            case "Словенский":
                return "sl";
            case "Суахили":
                return "sw";
            case "Сунданский":
                return "su";
            case "Таджикский":
                return "tg";
            case "Тайский":
                return "th";
            case "Тагальский":
                return "tl";
            case "Тамильский":
                return "ta";
            case "Татарский":
                return "tt";
            case "Телугу":
                return "te";
            case "Турецкий":
                return "tr";
            case "Удмуртский":
                return "udm";
            case "Узбекский":
                return "uz";
            case "Украинский":
                return "uk";
            case "Урду":
                return "ur";
            case "Финский":
                return "fi";
            case "Французский":
                return "fr";
            case "Хинди":
                return "hi";
            case "Хорватский":
                return "hr";
            case "Чешский":
                return "cs";
            case "Шведский":
                return "sv";
            case "Шотландский":
                return "gd";
            case "Эстонский":
                return "et";
            case "Эсперанто":
                return "eo";
            case "Яванский":
                return "jv";
            case "Японский":
                return "ja";
        }
        return "Ничего не вернул :(";
    }

    // Метод для запуска голосового ввода
    private void Speak() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak...");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Метод для запуска камеры, чтобы передать фотку и в следующем методе считать с нее текст
    private void getImage(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA_PHOTO);
    }

    // Метод для загрузки Истории из Shared Preferences
    private void loadHistory() {
        SharedPreferences sPref = getActivity().getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sPref.getString("history", null);
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        translateHistoryList = gson.fromJson(json, type);

        if (translateHistoryList == null) {
            translateHistoryList = new ArrayList<>();
        }
    }

    // Метод для сохранения Истории в Shared Preferences
    private void saveHistory(){
        SharedPreferences sPref = getActivity().getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(translateHistoryList);
        editor.putString("history", json);
        editor.apply();
        Toast.makeText(getContext(), "История переводов сохранена", Toast.LENGTH_SHORT).show();
    }

    // Метод для получения истории переводов
    private void getHistory(){
        Context popupContext = new ContextThemeWrapper(getContext(), R.style.popupMenu);
        PopupMenu popupMenu = new PopupMenu(popupContext, et_input);

        for (int i = 0; i < translateHistoryList.size(); i++){
            popupMenu.getMenu().add(i, Menu.FIRST + i, i, translateHistoryList.get(i));
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                et_input.setText(menuItem.getTitle());
                return false;
            }
        });

        popupMenu.show();
    }

    // Метод отвечающий за передачу голосового ввода в текст, а так же за передачу текста с картинки
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Получаем прослушанный текст
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                String result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
                et_input.setText(result);
            }
            else
                Toast.makeText(getContext(), "Не удалось вас понять...", Toast.LENGTH_SHORT).show();
        }

        // Получаем текст с картинки
        if (requestCode == REQUEST_CODE_CAMERA_PHOTO && resultCode == RESULT_OK) {
            // Фотка сделана, извлекаем картинку
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            // Подрубаем рекогнайзер
            TextRecognizer textRecognizer = new TextRecognizer.Builder(getContext()).build();
            // Передаем фотку во фрейм
            Frame frame = new Frame.Builder().setBitmap(photo).build();
            // Находим букавы )0)
            SparseArray<TextBlock> sparseArray = textRecognizer.detect(frame);
            // Подрубаем билдер строк
            StringBuilder stringBuilder = new StringBuilder();
            // Добаляем все слова в билдер
            for (int i=0; i < sparseArray.size(); i++) {
                TextBlock textBlock = sparseArray.get(i);
                String str = textBlock.getValue();
                stringBuilder.append(str);
            }
            // Сетим в эдит текст
            et_input.setText(stringBuilder);
        }
    }
}
