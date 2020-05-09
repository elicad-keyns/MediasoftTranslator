package com.ssoft.mediasofttranslator.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.ssoft.mediasofttranslator.MainActivity;
import com.ssoft.mediasofttranslator.R;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;

    private EditText et_input;
    private static EditText et_output;
    private ImageButton ib_translate;
    private ImageButton ib_share;
    private ImageButton ib_change;
    private Spinner s_input;
    private Spinner s_output;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        et_input = view.findViewById(R.id.et_input);
        et_output = view.findViewById(R.id.et_output);
        ib_translate = view.findViewById(R.id.ib_translate);
        ib_share = view.findViewById(R.id.ib_share);
        ib_change = view.findViewById(R.id.ib_change);
        s_input = view.findViewById(R.id.s_input);
        s_output = view.findViewById(R.id.s_output);

        ib_translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Translator", getLangCode(s_input) + "-" + getLangCode(s_output));
                MainActivity.getTranslate(et_input.getText().toString(), getLangCode(s_input), getLangCode(s_output));
            }
        });

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

        ib_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int inputPosition = s_input.getSelectedItemPosition();
                s_input.setSelection(s_output.getSelectedItemPosition());
                s_output.setSelection(inputPosition);
            }
        });

        return view;
    }

    public static void setOutput(String output){
        et_output.setText(output);
    }

    private String getLangCode(Spinner spinner){
        switch (spinner.getSelectedItem().toString()) {
            case "Азербайджанский": return "az";
            case "Албанский": return "sq";
            case "Амхарский": return "am";
            case "Английский": return "en";
            case "Арабский": return "ar";
            case "Армянский": return "hy";
            case "Африкаанс": return "af";
            case "Баскский": return "eu";
            case "Башкирский": return "ba";
            case "Белорусский": return "be";
            case "Бенгальский": return "bn";
            case "Бирманский": return "my";
            case "Болгарский": return "bg";
            case "Боснийский": return "bs";
            case "Вваллийский": return "cy";
            case "Венгерский": return "hu";
            case "Вьетнамский": return "vi";
            case "Гаитянский (креольский)": return "ht";
            case "Галисийский": return "gl";
            case "Голландский": return "nl";
            case "Горномарийский": return "mrj";
            case "Греческий": return "el";
            case "Грузинский": return "ka";
            case "Гуджарати": return "gu";
            case "Датский": return "da";
            case "Иврит": return "he";
            case "Идиш": return "yi";
            case "Индонезийский": return "id";
            case "Ирландский": return "ga";
            case "Итальянский": return "it";
            case "Исландский": return "is";
            case "Испанский": return "es";
            case "Казахский": return "kk";
            case "Каннада": return "kn";
            case "Каталанский": return "ca";
            case "Киргизский": return "ky";
            case "Китайский": return "zh";
            case "Корейский": return "ko";
            case "Коса": return "xh";
            case "Кхмерский": return "km";
            case "Лаосский": return "lo";
            case "Латынь": return "la";
            case "Латышский": return "lv";
            case "Литовский": return "lt";
            case "Люксембургский": return "lb";
            case "Малагасийский": return "mg";
            case "Малайский": return "ms";
            case "Малаялам": return "ml";
            case "Мальтийский": return "mt";
            case "Македонский": return "mk";
            case "Маори": return "mi";
            case "Маратхи": return "mr";
            case "Марийский": return "mhr";
            case "Монгольский": return "mn";
            case "Немецкий": return "de";
            case "Непальский": return "ne";
            case "Норвежский": return "no";
            case "Панджаби": return "pa";
            case "Папьяменто": return "pap";
            case "Персидский": return "fa";
            case "Польский": return "pl";
            case "Португальский": return "pt";
            case "Румынский": return "ro";
            case "Русский": return "ru";
            case "Себуанский": return "ceb";
            case "Сербский": return "sr";
            case "Сингальский": return "si";
            case "Словацкий": return "sk";
            case "Словенский": return "sl";
            case "Суахили": return "sw";
            case "Сунданский": return "su";
            case "Таджикский": return "tg";
            case "Тайский": return "th";
            case "Тагальский": return "tl";
            case "Тамильский": return "ta";
            case "Татарский": return "tt";
            case "Телугу": return "te";
            case "Турецкий": return "tr";
            case "Удмуртский": return "udm";
            case "Узбекский": return "uz";
            case "Украинский": return "uk";
            case "Урду": return "ur";
            case "Финский": return "fi";
            case "Французский": return "fr";
            case "Хинди": return "hi";
            case "Хорватский": return "hr";
            case "Чешский": return "cs";
            case "Шведский": return "sv";
            case "Шотландский": return "gd";
            case "Эстонский": return "et";
            case "Эсперанто": return "eo";
            case "Яванский": return "jv";
            case "Японский": return "ja";
        }
        return "Ничего не вернул :(";
    }
}
