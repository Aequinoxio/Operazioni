package com.example.utente.operazioni;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
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

    CustomKeyboard mCustomKeyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mCustomKeyboard= new CustomKeyboard(this, R.id.keyboardview, R.xml.hexkbd );
        mCustomKeyboard.registerEditText(R.id.edRisposta);

        lay=(LinearLayout)findViewById(R.id.layMain);
        colorBackground=lay.getSolidColor();

        EditText ed = (EditText) findViewById(R.id.edRisposta);
        ed.requestFocus();

        prossimaOperazione();
    }

    @Override public void onBackPressed() {
        // NOTE Trap the back key: when the CustomKeyboard is still visible hide it, only when it is invisible, finish activity
        if( mCustomKeyboard.isCustomKeyboardVisible() ) mCustomKeyboard.hideCustomKeyboard(); else this.finish();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // About
        if (id == R.id.action_about) {
            String s = getString(R.string.app_name) +" - Ver. " + BuildConfig.VERSION_NAME ;
            s+="\nby "+ getString(R.string.Autore);
            s+="\n\n"+getString(R.string.descrizione);
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.action_about)
                    .setMessage(s)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    }).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        // Animazione fade out del vecchio
        TextView txtView= (TextView) findViewById(R.id.txtOperazione);
        txtView.startAnimation(AnimationUtils.loadAnimation(this,android.R.anim.fade_out));
        if (moltiplOAddiz)
            txtView.setText(String.format("%d x %d", fattore1,fattore2));
        else
            txtView.setText(String.format("%d + %d", fattore1,fattore2));

        // Animazione fade in del nuovo
        txtView.startAnimation(AnimationUtils.loadAnimation(this,android.R.anim.fade_in));
    }

    public void checkResult(View v){
        EditText ed = (EditText) findViewById(R.id.edRisposta);

        String s= ed.getText().toString();
        if (s.isEmpty() || s.trim()=="")
            return;

        Integer guessed=Integer.parseInt(s);

        ed.setText("");

        TextView txtRes;
        txtRes=(TextView)findViewById(R.id.txtOperazione);

        if (guessed==risultato){
            txtRes.startAnimation(AnimationUtils.loadAnimation(this,R.anim.zoom_in));
            animateBackgroudColor(Color.GREEN,colorBackground,colorBackground);
            showToastResult("Ben fatto!!!\nProva con un'altra operazione");
            ultimaRisposta=true;
            risultatoOK++;
        } else {
            // Animazione dell'operazione (zoom)
            txtRes.startAnimation(AnimationUtils.loadAnimation(this,R.anim.zoom_out));
            animateBackgroudColor(Color.RED,colorBackground,colorBackground);
            showToastResult("Sbagliato\nRiprova");
            ultimaRisposta=false;
            risultatoKO++;
        }


        // Aggiorno le label di riepilogo. Può essere ottimizzato inserendo nei punti corretti delle IF le parti relative al risultato
        if (ultimaRisposta){
            txtRes=(TextView)findViewById(R.id.txtResultOk);

            txtRes.startAnimation(AnimationUtils.loadAnimation(this,android.R.anim.slide_in_left));
            txtRes.setText(String.valueOf(risultatoOK));

        } else {
            txtRes = (TextView) findViewById(R.id.txtResultKO);

            txtRes.startAnimation(AnimationUtils.loadAnimation(this,R.anim.slide_in_right));
            txtRes.setText(String.valueOf(risultatoKO));
        }
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
