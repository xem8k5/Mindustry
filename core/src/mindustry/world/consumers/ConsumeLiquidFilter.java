package mindustry.world.consumers;

import arc.struct.*;
import arc.func.Boolf;
import arc.scene.ui.layout.Table;
import mindustry.gen.*;
import mindustry.type.Liquid;
import mindustry.ui.Cicon;
import mindustry.ui.MultiReqImage;
import mindustry.ui.ReqImage;
import mindustry.world.Tile;
import mindustry.world.meta.BlockStat;
import mindustry.world.meta.BlockStats;
import mindustry.world.meta.values.LiquidFilterValue;

import static mindustry.Vars.content;

public class ConsumeLiquidFilter extends ConsumeLiquidBase{
    public final Boolf<Liquid> filter;

    public ConsumeLiquidFilter(Boolf<Liquid> liquid, float amount){
        super(amount);
        this.filter = liquid;
    }

    @Override
    public void applyLiquidFilter(Bits arr){
        content.liquids().each(filter, item -> arr.set(item.id));
    }

    @Override
    public void build(Tile tile, Table table){
        Array<Liquid> list = content.liquids().select(l -> !l.isHidden() && filter.get(l));
        MultiReqImage image = new MultiReqImage();
        list.each(liquid -> image.add(new ReqImage(liquid.icon(Cicon.medium), () -> tile.entity != null && tile.entity.getLiquids() != null && tile.entity.getLiquids().get(liquid) >= use(tile.entity))));

        table.add(image).size(8 * 4);
    }

    @Override
    public String getIcon(){
        return "icon-liquid-consume";
    }

    @Override
    public void update(Tilec entity){
        entity.getLiquids().remove(entity.getLiquids().current(), use(entity));
    }

    @Override
    public boolean valid(Tilec entity){
        return entity != null && entity.getLiquids() != null && filter.get(entity.getLiquids().current()) && entity.getLiquids().currentAmount() >= use(entity);
    }

    @Override
    public void display(BlockStats stats){
        stats.add(booster ? BlockStat.booster : BlockStat.input, new LiquidFilterValue(filter, amount * timePeriod, timePeriod == 60f));
    }
}
