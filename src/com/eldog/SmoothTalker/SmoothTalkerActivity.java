package com.eldog.SmoothTalker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class SmoothTalkerActivity extends Activity implements OnInitListener
{

    private static final int MY_DATA_CHECK_CODE = 0;

    private static final float NORMAL_PITCH = 1;
    private static final float LOW_PITCH = 0.5f;
    private static final float HIGH_PITCH = 1.5f;

    private static final Float[] pitches =
        { NORMAL_PITCH, LOW_PITCH, HIGH_PITCH };

    private static final float NORMAL_SPEECH_RATE = 1;
    private static final float SLOW_SPEECH_RATE = 0.5f;
    private static final float FAST_SPEECH_RATE = 2.0f;

    private static final Float[] speeds =
        { NORMAL_SPEECH_RATE, SLOW_SPEECH_RATE, FAST_SPEECH_RATE };
    
    private static final Map<Locale, Integer> hunkMap = new HashMap<Locale, Integer>();
    
    static
    {
        hunkMap.put(Locale.GERMANY, R.drawable.einstein);
        hunkMap.put(Locale.UK, R.drawable.sean_connery);
        hunkMap.put(Locale.US, R.drawable.tom_selleck);
        hunkMap.put(Locale.ITALY, R.drawable.berlusconi);
        hunkMap.put(Locale.FRANCE, R.drawable.descartes);
    }

    private ImageView hunkImage;
    
    private Spinner accentSpinner;
    private Spinner pitchSpinner;
    private Spinner speedSpinner;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        accentSpinner = (Spinner) findViewById(R.id.accent_spinner);
        pitchSpinner = (Spinner) findViewById(R.id.pitch_spinner);
        speedSpinner = (Spinner) findViewById(R.id.speed_spinner);
        hunkImage = (ImageView) findViewById(R.id.hunk_image);

        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
    }

    private TextToSpeech mTts;

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == MY_DATA_CHECK_CODE)
        {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS)
            {
                // success, create the TTS instance
                mTts = new TextToSpeech(this, this);
            } else
            {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent
                        .setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }

    @Override
    public void onInit(int arg0)
    {
        List<Locale> locales = new ArrayList<Locale>();
        for (Locale locale : Locale.getAvailableLocales())
        {
            if (mTts.isLanguageAvailable(locale) == TextToSpeech.LANG_COUNTRY_AVAILABLE)
            {
                locales.add(locale);
            }
        }
        if (locales.size() > 0)
        {
            mTts.setLanguage(locales.get(0));
        }
        ArrayAdapter<Locale> localeAdapter = new ArrayAdapter<Locale>(this,
                android.R.layout.simple_spinner_item, locales);
        localeAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accentSpinner.setAdapter(localeAdapter);
        accentSpinner.setOnItemSelectedListener(new LocaleSelectedListener());
        
        ArrayAdapter<Float> pitchAdapter = new ArrayAdapter<Float>(this,
                android.R.layout.simple_spinner_item, pitches);
        pitchAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pitchSpinner.setAdapter(pitchAdapter);
        pitchSpinner.setOnItemSelectedListener(new PitchSelectedListener());
        
        ArrayAdapter<Float> speechRateAdapter = new ArrayAdapter<Float>(this,
                android.R.layout.simple_spinner_item, speeds);
        speechRateAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        speedSpinner.setAdapter(speechRateAdapter);
        speedSpinner.setOnItemSelectedListener(new SpeechRateSelectedListener());
    }

    public class LocaleSelectedListener implements OnItemSelectedListener
    {

        public void onItemSelected(AdapterView<?> parent, View view, int pos,
                long id)
        {
            Locale locale = (Locale) parent.getItemAtPosition(pos);
            Toast
                    .makeText(
                            parent.getContext(),
                            "Accent set to "
                                    + locale.toString(),
                            Toast.LENGTH_SHORT).show();
            mTts.setLanguage(locale);
            
            if (locale.toString().equals("es_ES"))
            {
                hunkImage.setImageResource(R.drawable.antonio);
            }
            else if (locale.toString().equals("en_US_POSIX"))
            {
                hunkImage.setImageResource(R.drawable.ritchie);
            }
            else
            {
            
            Integer hunkId = hunkMap.get(locale);
            if (hunkId != null)
            {
                hunkImage.setImageResource(hunkMap.get(locale));
            }
            }

            
        }

        public void onNothingSelected(AdapterView parent)
        {
            // Do nothing.
        }
    }
    
    public class PitchSelectedListener implements OnItemSelectedListener
    {

        public void onItemSelected(AdapterView<?> parent, View view, int pos,
                long id)
        {
            Toast
                    .makeText(
                            parent.getContext(),
                            "Pitch set to "
                                    + parent.getItemAtPosition(pos).toString(),
                            Toast.LENGTH_SHORT).show();
            mTts.setPitch((Float) parent.getItemAtPosition(pos));
        }

        public void onNothingSelected(AdapterView parent)
        {
            // Do nothing.
        }
    }
    
    public class SpeechRateSelectedListener implements OnItemSelectedListener
    {

        public void onItemSelected(AdapterView<?> parent, View view, int pos,
                long id)
        {
            Toast
                    .makeText(
                            parent.getContext(),
                            "Speed set to "
                                    + parent.getItemAtPosition(pos).toString(),
                            Toast.LENGTH_SHORT).show();
            mTts.setSpeechRate((Float) parent.getItemAtPosition(pos));
        }

        public void onNothingSelected(AdapterView parent)
        {
            // Do nothing.
        }
    }
    
    private static Random generator = new Random();

    public void onSpeakClick(View view)
    {
        String chatupLine = chatupLines[generator.nextInt(chatupLines.length)];
        mTts.speak(chatupLine, TextToSpeech.QUEUE_FLUSH, null);
    }
    
    private static final String[] chatupLines = { 
            "I only have 3 months left to live, Let's have a party in your pants",
            "Me. You. Now.",
            "Want to see my extensive and assorted collection of cables?",
            "Would you touch me so I can tell my friends I've been touched by an angel?",
            "Are you accepting applications for your fan club",
            "I miss my teddy bear. Would you sleep with me?",
            "God was just showing off when He made you",
            "I don't speak in tongues, but I kiss that way!",
            "If you've lost your virginity, can I have the box it came in?",
            "There are 256 bones in your body! Would you like another",
            "Would you like to stroke my lucky scrotum?",
            "Is your father a thief? Because someone stole the stars from the sky and put them in your eyes",
            "Sexist? What's wrong with being sexy?",
            "Grr baby, Grr",
            "I can implement sort algorithms which run in N log N time",
            "It takes 2 to tango, let us therefore copulate",
            "I have a 10 year plan",
            "Get down. Make Love.",
            "J'adore vous, Mon petit poisson",
            "Let us run away together, we'll take the M6 to Reading",
            "You could be my precious croissant",
            "I would eat a kitten to be with you?",
            "Your smile makes my member weep tears of joy",
            "There is no such thing as a slut",
            "Boogie on my bell",
            "Let me confgure you home network?",
            "I need sudo access to your slash var"
    };
    

}