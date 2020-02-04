package mindustry.world.blocks.storage;

import arc.*;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.gen.*;
import mindustry.game.EventType.*;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemType;
import mindustry.world.Tile;
import mindustry.world.meta.BlockStat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.data;
import static mindustry.Vars.world;

public class LaunchPad extends StorageBlock{
    public final int timerLaunch = timers++;
    /** Time inbetween launches. */
    public float launchTime;

    public LaunchPad(String name){
        super(name);
        update = true;
        hasItems = true;
        solid = true;
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(BlockStat.launchTime, launchTime / 60f, StatUnit.seconds);
    }

    @Override
    public boolean acceptItem(Item item, Tile tile, Tile source){
        return item.type == ItemType.material && tile.entity.getItems().total() < itemCapacity;
    }

    @Override
    public void draw(Tile tile){
        super.draw(tile);

        float progress = Mathf.clamp(Mathf.clamp((tile.entity.getItems().total() / (float)itemCapacity)) * ((tile.entity.timerTime(timerLaunch) / (launchTime / tile.entity.timeScale))));
        float scale = size / 3f;

        Lines.stroke(2f);
        Draw.color(Pal.accentBack);
        Lines.poly(tile.drawx(), tile.drawy(), 4, scale * 10f * (1f - progress), 45 + 360f * progress);

        Draw.color(Pal.accent);

        if(tile.entity.getCons().valid()){
            for(int i = 0; i < 3; i++){
                float f = (Time.time() / 200f + i * 0.5f) % 1f;

                Lines.stroke(((2f * (2f - Math.abs(0.5f - f) * 2f)) - 2f + 0.2f));
                Lines.poly(tile.drawx(), tile.drawy(), 4, (1f - f) * 10f * scale);
            }
        }

        Draw.reset();
    }

    @Override
    public void update(Tile tile){
        Tilec entity = tile.entity;

        if(world.isZone() && entity.consValid() && entity.getItems().total() >= itemCapacity && entity.timer(timerLaunch, launchTime / entity.timeScale)){
            for(Item item : Vars.content.items()){
                Events.fire(Trigger.itemLaunch);
                Fx.padlaunch.at(tile);
                int used = Math.min(entity.getItems().get(item), itemCapacity);
                data.addItem(item, used);
                entity.getItems().remove(item, used);
                Events.fire(new LaunchItemEvent(item, used));
            }
        }
    }
}
