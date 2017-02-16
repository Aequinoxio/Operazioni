package com.example.utente.operazioni;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity {

    private int fattore1, fattore2, risultato;
    private int risultatoOK=0, risultatoKO=0;
    private boolean ultimaRisposta=false;
    private boolean moltiplOAddiz=true;  // True se mostro le moltiplicazioni, false per le addizioni

    // Mi occorre per resettare il colore in background dopo una risposta
    LinearLayout lay;
    int colorBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        lay=(LinearLayout)findViewById(R.id.layMain);
        colorBackground=lay.getSolidColor();

        EditText ed = (EditText) findViewById(R.id.edRisposta);
        ed.requestFocus();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(ed, InputMethodManager.SHOW_IMPLICIT);
        prossimaOperazione();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    private void prossimaOperazione(){
        Random n=new Random(System.currentTimeMillis());

        // Moltiplicazione
        if (moltiplOAddiz) {
            fattore1=n.nextInt(11);
            fattore2=n.nextInt(11);
            risultato = fattore1 * fattore2;
        }
        // Somma
        else {
            fattore1=n.nextInt(101);
            fattore2=n.nextInt(11);
            risultato = fattore1 + fattore2;
        }
        TextView txtView= (TextView) findViewById(R.id.txtOperazione);

        if (moltiplOAddiz)
            txtView.setText(String.format("%d x %d", fattore1,fattore2));
        else
            txtView.setText(String.format("%d + %d", fattore1,fattore2));
    }

    public void checkResult(View v){
        EditText ed = (EditText) findViewById(R.id.edRisposta);

        String s= ed.getText().toString();
        if (s.isEmpty() || s.trim()=="")
            return;

        Integer guessed=Integer.parseInt(s);

        ed.setText("");

        if (guessed==risultato){
            animateBackgroudColor(Color.GREEN,colorBackground,colorBackground);
            showToastResult("Ben fatto!!!\nProva con un'altra operazione");
            ultimaRisposta=true;
            risultatoOK++;
        } else {
            animateBackgroudColor(Color.RED,colorBackground,colorBackground);
            showToastResult("Sbagliato\nRiprova");
            ultimaRisposta=false;
            risultatoKO++;
        }

        // Aggiorno le label di riepilogo. Può essere ottimizzato inserendo nei punti corretti delle IF le parti relative al risultato
        TextView txtRes=(TextView)findViewById(R.id.txtResultOk);
        txtRes.setText(String.valueOf(risultatoOK));
        txtRes=(TextView)findViewById(R.id.txtResultKO);
        txtRes.setText(String.valueOf(risultatoKO));
    }

    // Preso dalla documentazione ufficiale di google https://developer.android.com/guide/topics/ui/controls/radiobutton.html
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        boolean moltAddTemp=false;

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButtonAdd:
                if (checked)
                    moltAddTemp=false;

                break;
            case R.id.radioButtonMolt:
                if (checked)
                    moltAddTemp=true;

                break;
        }

        // Se ho cliccato sullo stesso radio button non faccio nulla
        if (moltiplOAddiz!=moltAddTemp) {
            moltiplOAddiz=moltAddTemp;
            prossimaOperazione();
        }
    }

    private void showToastResult(String s){
        Toast toast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    private void animateBackgroudColor(int colorFrom, int colorTo, int colorNormal){
        ///
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(1000); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                //TextView textView=(TextView)findViewById(R.id.txtResultOk);
                //textView.setBackgroundColor((int) animator.getAnimatedValue());
                lay.setBackgroundColor((int) animator.getAnimatedValue());
            }
        });

        colorAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                lay.setBackgroundColor(colorBackground);
                if (ultimaRisposta)
                    prossimaOperazione();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        colorAnimation.start();
    }
}
