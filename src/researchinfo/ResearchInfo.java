package researchinfo;

import arc.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;

public class ResearchInfo extends Mod{
    public ResearchInfo(){
        Events.on(ClientLoadEvent.class, e -> {
            //TODO stat madness
        });
    }

    @Override
    public void loadContent(){
        //No content to laod
    }
}
