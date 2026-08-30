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
        // FogControl의 WorldLoadEvent 리스너(엔진 내부, 우리보다 먼저 등록됨)가
        // 먼저 실행되어 초기 static fog 데이터를 만든 "다음"에 우리 리스너가 실행되어
        // 그 데이터를 곧바로 전부 "발견됨"으로 덮어씁니다.
        Events.on(WorldLoadEvent.class, e -> {
            if (Vars.state == null || Vars.state.rules == null) return;

            var rules = Vars.state.rules;

            // 1) 이후 프레임에서 안개 시스템 자체가 다시 안개를 그리지 않도록 규칙도 꺼둠
            rules.fog = false;
            rules.staticFog = false;
            rules.lighting = false;
            rules.ambientLight = new Color(1f, 1f, 1f, 0f);

            // 2) 이미 만들어진 "미발견" 데이터 자체를 전부 발견 처리
            if (Vars.fogControl != null) {
                for (Team team : Team.all) {
                    Bits discovered = Vars.fogControl.getDiscovered(team);
                    if (discovered != null) {
                        discovered.set(0, discovered.numBits());
                    }
                }
                Log.info("[FogDelete] 모든 팀 static fog 데이터를 전부 발견 처리함");
            }

            // 3) CPU 쪽 데이터를 GPU 텍스처로 강제 재동기화 -> 화면에 즉시 반영
            if (Vars.renderer != null && Vars.renderer.fog != null) {
                Vars.renderer.fog.copyFromCpu();
                Log.info("[FogDelete] FogRenderer.copyFromCpu() 강제 호출함");
            }
        });
    }
}
