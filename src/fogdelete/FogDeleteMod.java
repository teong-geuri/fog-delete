package fogdelete;

import mindustry.mod.Mod;
import mindustry.Vars;
import mindustry.game.EventType.WorldLoadEvent;
import arc.Events;
import arc.graphics.Color;
import arc.util.Log;

public class FogDeleteMod extends Mod {

    public FogDeleteMod() {
        Log.info("[FogDelete] 모드 로드됨");
    }

    @Override
    public void init() {
        Events.on(WorldLoadEvent.class, e -> {
            if (Vars.state == null || Vars.state.rules == null) return;

            var rules = Vars.state.rules;

            // 1) 안개(Fog of War) 완전 제거
            rules.fog = false;
            rules.staticFog = false;

            // 2) 어둠(조명 시스템으로 인한 검은 가림막) 제거
            rules.lighting = false;
            rules.ambientLight = new Color(1f, 1f, 1f, 0f);

            Log.info("[FogDelete] fog/staticFog/lighting 전부 비활성화 적용됨");
        });
    }
}
