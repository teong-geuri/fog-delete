package fogdelete;

import mindustry.mod.Mod;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.game.EventType.WorldLoadEvent;
import arc.Events;
import arc.struct.Bits;
import arc.graphics.Color;
import arc.util.Log;

public class FogDeleteMod extends Mod {

    public FogDeleteMod() {
        Log.info("[FogDelete] 모드 로드됨");
    }

    @Override
    public void init() {
        // 블록 그림자 제거 - 콘텐츠는 한 번만 로드되므로 init()에서 한 번만 처리
        int count = 0;
        for (var block : Vars.content.blocks()) {
            if (block.hasShadow) {
                block.hasShadow = false;
                count++;
            }
        }
        Log.info("[FogDelete] 블록 @개의 그림자 비활성화함", count);

        // 기존 안개/어둠 제거 로직
        Events.on(WorldLoadEvent.class, e -> {
            if (Vars.state == null || Vars.state.rules == null) return;

            var rules = Vars.state.rules;
            rules.fog = false;
            rules.staticFog = false;
            rules.lighting = false;
            rules.ambientLight = new Color(1f, 1f, 1f, 0f);

            if (Vars.fogControl != null) {
                for (Team team : Team.all) {
                    Bits discovered = Vars.fogControl.getDiscovered(team);
                    if (discovered != null) {
                        discovered.set(0, discovered.numBits());
                    }
                }
            }

            if (Vars.renderer != null && Vars.renderer.fog != null) {
                Vars.renderer.fog.copyFromCpu();
            }
        });
    }
}
