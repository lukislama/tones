package sample;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Tone
{
    float hz;
    float dur;
    int volume = 100;

    Tone(float hz, float dur)
    {
        this.hz = hz;
        this.dur = dur;
    }

    public void play() throws LineUnavailableException
    {
        float frequency = 44100;
        byte[] buf;
        AudioFormat af;

        buf = new byte[1];
        af = new AudioFormat(frequency,8,1,true,false);
        SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
        sdl = AudioSystem.getSourceDataLine(af);
        sdl.open(af);
        sdl.start();
        for(int i=0; i<dur*frequency/1000; i++)
        {
            double angle = i / (frequency / hz) * 2.0 * Math.PI;
            buf[0] = (byte) (Math.sin(angle) * volume);
            sdl.write(buf,0,1);
        }

        sdl.drain();
        sdl.stop();
        sdl.close();

    }
}
