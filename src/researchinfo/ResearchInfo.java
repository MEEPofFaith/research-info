package researchinfo;

import arc.*;
import arc.graphics.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.content.*;
import mindustry.content.TechTree.*;
import mindustry.ctype.*;
import mindustry.game.EventType.*;
import mindustry.game.Objectives.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.mod.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.meta.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class ResearchInfo extends Mod{
    public static final Stat researchInfo = new Stat("ri-research-info");

    public ResearchInfo(){
        Events.on(ClientLoadEvent.class, e -> {
            loadSettings();

            //Add research info to the stats of every block and unit
            TechTree.all.each(t -> !t.content.alwaysUnlocked &&
                    (t.content instanceof Block || t.content instanceof UnitType) &&
                    (t.parent != null || t.requirements.length > 0 || t.objectives.contains(o -> !(o instanceof Research r) || !(r.content instanceof Item))), t -> {
                t.content.stats.add(researchInfo, table -> researchInfo(table, t));
            });
        });
    }

    @Override
    public void loadContent(){
        //No content to laod
    }

    private void loadSettings(){
        ui.settings.addCategory(bundle.get("setting.ri-title"), Icon.tree, t -> { //Literally just one setting, amazing
            t.checkPref("ri-verbose-resources", false);
        });
    }

    //void so that not every instance of this StatValue needs this code within it; only one instance of this code.
    public static void researchInfo(Table table, TechNode node){
        Table rc = new Table(Tex.button, t -> {
            t.left().defaults().left();
            if(node.parent != null){
                divider(t, "@previous", Pal.accent);
                UnlockableContent c = node.parent.content;
                t.add((c.hasEmoji() ? c.emoji() + " " : "") + c.localizedName).color(Color.lightGray);
                t.row();
            }
            if(node.requirements.length > 0){
                divider(t, "@resources", Pal.accent);
                if(settings.getBool("ri-verbose-resources", false)){
                    for(ItemStack req : node.requirements){
                        t.table(list -> {
                            list.left();
                            list.image(req.item.uiIcon).size(8 * 3).padRight(3);
                            list.add(req.item.localizedName + ": " + req.amount).color(Color.lightGray);
                        }).fillX();
                        t.row();
                    }
                }else{
                    t.table(list -> {
                        for(ItemStack stack : node.requirements){
                            list.add(new ItemDisplay(stack.item, stack.amount, false)).padRight(5);
                        }
                    });
                    t.row();
                }
            }
            Seq<Objective> displayObjs = node.objectives.select(o -> !(o instanceof Research r) || !(r.content instanceof Item));
            if(displayObjs.size > 0){
                divider(t, "@objectives", Pal.accent);
                for(Objective o : displayObjs){
                    String text = "> ";
                    if(o instanceof Research r){ //Hardcode this because otherwise it would show as ??? if not complete in campaign.
                        UnlockableContent c = r.content;
                        text += bundle.format("requirement.research", (c.hasEmoji() ? c.emoji() + " " : "") + c.localizedName);
                    }else{
                        text += o.display();
                    }
                    t.add(text).color(Color.lightGray);
                    t.row();
                }
            }
        });
        Collapser coll = new Collapser(rc, true);
        coll.setDuration(0.1f);

        table.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(false)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).size(8).padLeft(16f).expandX().left();
        table.row();
        table.add(coll).left().padTop(3).colspan(2);
    }

    static void divider(Table t, String label, Color color){
        t.add(label).growX().left().color(color);
        t.row();
        t.image().growX().pad(5f).padLeft(0f).padRight(0f).height(3f).color(color);
        t.row();
    }
}
