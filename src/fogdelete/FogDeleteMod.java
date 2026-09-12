package fogdelete;

import mindustry.mod.Mod;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.game.EventType.WorldLoadEvent;
import mindustry.game.EventType.Trigger;
import arc.Events;
import arc.struct.Bits;
import arc.util.Log;

public class FogDeleteMod extends Mod {

    // WorldLoadEvent 때 이미 정적(스태틱) 안개를 걷어냈는지 (맵 로드마다 한 번이면 충분)
    private boolean staticFogCleared = false;

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

        // 맵이 새로 로드될 때마다 "이미 밝힌 지역" 비트를 다시 초기화해야 함
        Events.on(WorldLoadEvent.class, e -> {
            staticFogCleared = false;
            applyNoFog();
        });

        // 핵심 수정: Control.playMap() 등에서는 WorldLoadEvent가 발생한 "직후"에
        // state.rules 객체 자체를 맵/게임모드의 새 Rules 인스턴스로 통째로 교체한다.
        // 그래서 WorldLoadEvent 한 번만 처리하면 그 직후 rules.fog가 다시 true로
        // 돌아가 안개가 사라지지 않는 것처럼 보인다.
        // 이를 막기 위해 매 틱마다 규칙을 강제로 재적용해서 무엇이 rules를
        // 바꿔치기하더라도 항상 안개가 꺼진 상태를 유지하도록 한다.
        Events.run(Trigger.update, this::applyNoFog);
    }

    private void applyNoFog() {
        if (Vars.state == null || Vars.state.rules == null) return;

        var rules = Vars.state.rules;
        rules.fog = false;
        rules.staticFog = false;
        rules.lighting = false;
        // 새 Color 객체를 매 틱 할당하지 않도록 기존 객체를 재사용
        rules.ambientLight.set(1f, 1f, 1f, 0f);

        if (!staticFogCleared && Vars.fogControl != null) {
            boolean anyCleared = false;
            for (Team team : Team.all) {
                Bits discovered = Vars.fogControl.getDiscovered(team);
                if (discovered != null && discovered.numBits() > 0) {
                    discovered.set(0, discovered.numBits());
                    anyCleared = true;
                }
            }
            if (anyCleared) {
                staticFogCleared = true;
                if (Vars.renderer != null && Vars.renderer.fog != null) {
                    Vars.renderer.fog.copyFromCpu();
                }
            }
        }
    }
}
