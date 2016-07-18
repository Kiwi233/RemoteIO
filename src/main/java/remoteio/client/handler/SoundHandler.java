package remoteio.client.handler;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author dmillerw
 */
public class SoundHandler {
    public static final SoundHandler INSTANCE = new SoundHandler();

    public BlockPos pos;

    private boolean translate = false;

    public void translateNextSound(BlockPos pos) {
        translate = true;
        this.pos = pos;
    }

    @SubscribeEvent
    public void onSoundPlayed(PlaySoundEvent event) {
        if (translate) {
            ISound sound = event.getSound();
            event.getResultSound() = new PositionedSoundRecord(sound.getSoundLocation(), sound.getCategory(), sound.getVolume(), sound.getPitch(), sound.canRepeat(), sound.getRepeatDelay(), sound.getAttenuationType(), sound.getXPosF(), sound.getYPosF(), sound.getZPosF()).getSound();
            translate = false;
        }
    }
}