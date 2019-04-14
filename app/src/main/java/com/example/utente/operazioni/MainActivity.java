package com.example.utente.operazioni;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity {

    private int fattore1, fattore2, risultato;
    private int risultatoOK=0, risultatoKO=0;
    private boolean ultimaRisposta=false;
    //private boolean moltiplOAddiz=true;  // True se mostro le moltiplicazioni, false per le addizioni

    enum TipoOperazioni {
        Addizione, Sottrazione, Divisione, Moltiplicazione, Random;
        private static final Random numberGenerator = new Random();

//        // Todo verificare perchè occorre un parametro. l'enum è fisso...
//        public static <T> T randomElement(T[] elements){
//            return elements[numberGenerator.nextInt(elements.length-1)];
//        }

        public static TipoOperazioni randomElement(){
            return values()[numberGenerator.nextInt(TipoOperazioni.values().length-1)];
        }
    }

    // Memorizo il tipo di operazione selezionato e quello precedente
    private TipoOperazioni TipoCorrente=TipoOperazioni.Moltiplicazione;
//    TipoOperazioni TipoRandom=TipoOperazioni.Addizione.randomElement(TipoOperazioni.values());

    // Mi occorre per resettare il colore in background dopo una risposta
    private FrameLayout lay;
    private int colorBackground;

    // Per la tastiera personalizzata
    private CustomKeyboard mCustomKeyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mCustomKeyboard= new CustomKeyboard(this, R.id.keyboardview, R.xml.keyboard);
        mCustomKeyboard.registerEditText(R.id.edRisposta);

        //lay=(LinearLayout)findViewById(R.id.layMain);
        lay=(FrameLayout)findViewById(R.id.layGlobal);
        colorBackground=((ColorDrawable) lay.getBackground()).getColor();
                //getSolidColor();

        EditText ed = (EditText) findViewById(R.id.edRisposta);
        ed.requestFocus();

        // Preimposto il toggle
        ToggleButton t = (ToggleButton)findViewById(lastButtonPressed);
        t.setChecked(true);

        // Imposto la prima operazione
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
            showAboutDialog();


//            String s = getString(R.string.app_name) +" - Ver. " + BuildConfig.VERSION_NAME ;
//            s+="\nby "+ getString(R.string.Autore);
//            s+="\n\n"+getString(R.string.descrizione);
//            new AlertDialog.Builder(MainActivity.this)
//                    .setTitle(R.string.action_about)
//                    .setMessage(s)
//                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            dialog.cancel();
//                        }
//                    }).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAboutDialog() {
        BufferedReader bufferedReader = null;
        StringBuffer testo = new StringBuffer();
        testo.append(getString(R.string.app_name) +" - Ver. " + BuildConfig.VERSION_NAME+"\n");
        testo.append("by "+ getString(R.string.Autore)+"\n\n");

        // Leggo il readme dalla folder raw
        try {
            // Se dovessi metterla nella directory assets va usata questa riga
           // bufferedReader = new BufferedReader(new InputStreamReader(getAssets().open("readme.txt")));
            bufferedReader=new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.readme)));
            String linea;
            while ((linea = bufferedReader.readLine()) != null) {
                testo.append(linea+"\n");
            }


        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ioe2) {
                    ioe2.printStackTrace();
                }
            }
        }

        // Devo impostare uno stile con l'esplicita indicazione a mostrare il titolo della finestra
        Dialog dialog = new Dialog(this, R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        dialog.setContentView(R.layout.about_dialog);
        dialog.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.about_dialog_custom_title);

        TextView textView = (TextView) dialog.findViewById(R.id.txtAboutDialog);
        textView.setText(testo);
        // dialog.setTitle(R.string.action_about);

        dialog.show();
    }

    final Random rndFattore=new Random(System.currentTimeMillis());
    private void prossimaOperazione(){
        // Animazione fade out del vecchio
        TextView txtView= (TextView) findViewById(R.id.txtOperazione);
        txtView.startAnimation(AnimationUtils.loadAnimation(this,android.R.anim.fade_out));

        // Per rendere più randomica la selezione delle operazioni,
        // per ogni estrazione faccio un numero casuale (tra 1 e 100) di estrazioni casuali tra 1 e 10
        int dummyVal1, dummyVal=rndFattore.nextInt(101);
        for(int i=0;i<dummyVal;i++){
            dummyVal1=rndFattore.nextInt(11);
        }

        // Seleziono il tipo operazione
        TipoOperazioni TipoOperazioniTemp;
        if (TipoCorrente==TipoOperazioni.Random){
            TipoOperazioniTemp=TipoOperazioni.randomElement();
        } else {
            TipoOperazioniTemp=TipoCorrente;
        }

        // Calcolo gli elementi dell'operazione
        int temp;
        switch (TipoOperazioniTemp){
            case Moltiplicazione :
                fattore1=rndFattore.nextInt(10)+1;
                fattore2=rndFattore.nextInt(10)+1;
                risultato = fattore1 * fattore2;
                txtView.setText(String.format("%d x %d", fattore1,fattore2));

                break;

            case Divisione :
                temp = rndFattore.nextInt(11);
                fattore2=rndFattore.nextInt(10)+1;  // Prevengo lo 0 come divisore
                fattore1=temp*fattore2;
                risultato=temp;
                txtView.setText(String.format("%d : %d", fattore1,fattore2));

                break;

            case Addizione :
                fattore1=rndFattore.nextInt(100)+1;
                fattore2=rndFattore.nextInt(10)+1;
                risultato = fattore1 + fattore2;
                txtView.setText(String.format("%d + %d", fattore1,fattore2));
                break;

            case Sottrazione:
                temp=rndFattore.nextInt(51);
                fattore2=rndFattore.nextInt(11);
                fattore1=temp+fattore2;
                risultato=temp;
                txtView.setText(String.format("%d - %d", fattore1,fattore2));
                break;
        }

        // Animazione fade in del nuovo
        txtView.startAnimation(AnimationUtils.loadAnimation(this,android.R.anim.fade_in));
    }

    public void onCheckResult(View v){
        EditText ed = (EditText) findViewById(R.id.edRisposta);

        String s= ed.getText().toString();
        if (s.isEmpty() || s.trim().equals(""))
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

    private int lastButtonPressed=R.id.toggleMolt;

    // Preso dalla documentazione ufficiale di google https://developer.android.com/guide/topics/ui/controls/radiobutton.html
    public void onToggleButtonClicked(View view) {
        // Is the button now checked?
//        boolean checked = ((RadioButton) view).isChecked();
        boolean checked = ((ToggleButton) view).isChecked();
        // Se ho premuto lo stesso pulsante esco
        if (view.getId()==lastButtonPressed) {
            ((ToggleButton) view).setChecked(true);
            return;
        }

        // Spengo il vecchio pulsante
        ToggleButton t = (ToggleButton)findViewById(lastButtonPressed);
        t.setChecked(false);

        lastButtonPressed=view.getId();

        TipoOperazioni TipoBuffer = TipoCorrente;

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.toggleAdd:
                if (checked)
                    TipoBuffer=TipoOperazioni.Addizione;
                break;

            case R.id.toggleMolt:
                if (checked)
                    TipoBuffer=TipoOperazioni.Moltiplicazione;
                break;

            case R.id.toggleDiv:
                if (checked)
                    TipoBuffer=TipoOperazioni.Divisione;
                break;

            case R.id.toggleSott:
                if (checked)
                    TipoBuffer=TipoOperazioni.Sottrazione;
                break;

            case R.id.toggleRandom:
                if (checked)
                    TipoBuffer=TipoOperazioni.Random;
                break;
        }

        // Se ho cliccato sullo stesso radio button non faccio nulla
        if (TipoCorrente!=TipoBuffer) {
            TipoCorrente=TipoBuffer;
            prossimaOperazione();
        }
    }

    // Preso dalla documentazione ufficiale di google https://developer.android.com/guide/topics/ui/controls/radiobutton.html
//    public void onRadioButtonClicked(View view) {
//        // Is the button now checked?
////        boolean checked = ((RadioButton) view).isChecked();
//        boolean checked = ((ToggleButton) view).isChecked();
//        // Se ho premuto lo stesso pulsante esco
//        if (view.getId()==lastButtonPressed) {
//            ((ToggleButton) view).setChecked(true);
//            return;
//        }
//
//        // Spengo il vecchio pulsante
//        ToggleButton t = (ToggleButton)findViewById(lastButtonPressed);
//        t.setChecked(false);
//
//        lastButtonPressed=view.getId();
//
//        TipoOperazioni TipoBuffer = TipoCorrente;
//
//        // Check which radio button was clicked
//        switch(view.getId()) {
//            case R.id.radioButtonAdd:
//                if (checked)
//                    TipoBuffer=TipoOperazioni.Addizione;
//                break;
//
//            case R.id.radioButtonMolt:
//                if (checked)
//                    TipoBuffer=TipoOperazioni.Moltiplicazione;
//                break;
//
//            case R.id.radioButtonDiv:
//                if (checked)
//                    TipoBuffer=TipoOperazioni.Divisione;
//                break;
//
//            case R.id.radioButtonSott:
//                if (checked)
//                    TipoBuffer=TipoOperazioni.Sottrazione;
//                break;
//
//        }
//
//        // Se ho cliccato sullo stesso radio button non faccio nulla
//        if (TipoCorrente!=TipoBuffer) {
//            TipoCorrente=TipoBuffer;
//            prossimaOperazione();
//        }
//    }

    private void showToastResult(String s){
        Toast toast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 0);
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
