package com.microsoft.sdksample;

import android.app.AlertDialog;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.microsoft.speech.tts.Synthesizer;
import com.microsoft.speech.tts.Voice;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import omrecorder.AudioChunk;
import omrecorder.AudioRecordConfig;
import omrecorder.OmRecorder;
import omrecorder.PullTransport;
import omrecorder.PullableSource;
import omrecorder.Recorder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends ActionBarActivity {
    private Synthesizer m_syn;
    private static final String TAG = "Main Activity";

    private Button play, stop, record;
    private String outputFile;
    private MediaRecorder myAudioRecorder;

    Recorder recorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        play = (Button) findViewById(R.id.play);
        stop = (Button) findViewById(R.id.stop);
        record = (Button) findViewById(R.id.record);
        stop.setEnabled(false);
        play.setEnabled(false);
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    myAudioRecorder.prepare();
                    myAudioRecorder.start();
                } catch (IllegalStateException ise) {
                    // make something ...
                } catch (IOException ioe) {
                    // make something
                }
                record.setEnabled(false);
                stop.setEnabled(true);
                Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAudioRecorder.stop();
                myAudioRecorder.release();
                myAudioRecorder = null;
                record.setEnabled(true);
                stop.setEnabled(false);
                play.setEnabled(true);
                Toast.makeText(getApplicationContext(), "Audio Recorder successfully", Toast.LENGTH_LONG).show();
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(outputFile);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    Toast.makeText(getApplicationContext(), "Playing Audio", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    // make something
                }
            }
        });

        /////////////////////////////////////////////////////////////////
        if (getString(R.string.api_key).startsWith("Please")) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.add_subscription_key_tip_title))
                    .setMessage(getString(R.string.add_subscription_key_tip))
                    .setCancelable(false)
                    .show();
        } else {

            if (m_syn == null) {
                // Create Text To Speech Synthesizer.
                m_syn = new Synthesizer(getString(R.string.api_key));
            }

            Toast.makeText(this, "If the wave is not played, please see the log for more information.", Toast.LENGTH_LONG).show();

            m_syn.SetServiceStrategy(Synthesizer.ServiceStrategy.AlwaysService);

            Voice v = new Voice("en-US", "Microsoft Server Speech Text to Speech Voice (en-US, ZiraRUS)", Voice.Gender.Female, true);
            //Voice v = new Voice("zh-CN", "Microsoft Server Speech Text to Speech Voice (zh-CN, HuihuiRUS)", Voice.Gender.Female, true);
            m_syn.SetVoice(v, null);

            // Use a string for speech.
            m_syn.SpeakToAudio(getString(R.string.tts_text));

            // Use SSML for speech.
            // String text = "<speak>This output speech uses SSML.</speak>";
            //m_syn.SpeakSSMLToAudio(text);

            findViewById(R.id.stop_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    m_syn.stopSound();
                }
            });

            findViewById(R.id.play_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    m_syn.SpeakToAudio(getString(R.string.tts_text));
                }
            });
        }

        MediaPlayer player = new MediaPlayer();
        String path = "android.resource://com.microsoft.sdksample/raw/forhan";
        try {
            player.setDataSource(getApplicationContext(), Uri.parse(path));
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Uri uri = Uri.parse("android.resource://com.microsoft.sdksample/raw/forhan");
        //File audioFile = new File("android.resource://com.microsoft.sdksample/raw/forhan");
        //String filePath = getRealPathFromURIPath(uri, MainActivity.this);
        //File file = new File(filePath);
        File audioFile = new File(outputFile);

        //File file = new File(getRealPathFromURI());
        final RequestBody requestAudioFile = RequestBody.create(MediaType.parse("application/octet-stream"), audioFile);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostWav service = PostWav.retrofit.create(PostWav.class);
                Call<ResponseBody> call = service.send("audio/wav; samplerate=1600", requestAudioFile, "6149675feef147f797f24802e78aafac");
                call.enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.d(TAG, "Upload success");
                        //RequestError error = ErrorUtils.parseError(response);
                        //Log.d("error message", error.toString());
                        Log.d(TAG, "Response: " + response.body().toString());
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d(TAG, "Upload error: " + t.getMessage());

                    }
                });
                //TextView textView = (TextView) findViewById(R.id.textView);
                //textView.setText(result);
            }
        });
    }

    private void animateVoice(final float maxPeak) {
        record.animate().scaleX(1 + maxPeak).scaleY(1 + maxPeak).setDuration(10).start();
    }

    private PullableSource mic() {
        return new PullableSource.Default(
                new AudioRecordConfig.Default(
                        MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT,
                        AudioFormat.CHANNEL_IN_MONO, 44100
                )
        );
    }

    @NonNull
    private File file() {
        return new File(Environment.getExternalStorageDirectory(), "hunter.wav");
    }


    private void setupRecorder() {
        recorder = OmRecorder.wav(
                new PullTransport.Default(mic(), new PullTransport.OnAudioChunkPulledListener() {
                    @Override public void onAudioChunkPulled(AudioChunk audioChunk) {
                        animateVoice((float) (audioChunk.maxAmplitude() / 200.0));
                    }
                }), file());
    }
}

