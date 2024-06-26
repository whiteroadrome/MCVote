package com.whiteroad;

import org.bukkit.command.CommandSender;

public class SendMessage {

    public static void help(CommandSender sender) {
        sender.sendMessage("사용 가능한 명령어:");
        sender.sendMessage("/company 창설 [회사명] - 새로운 회사를 생성합니다.");
        sender.sendMessage("/company 폐업 [회사명] - 지정된 회사를 삭제합니다.");
        sender.sendMessage("/company 정보 [회사명] - 회사 정보를 조회합니다.");
        sender.sendMessage("/company 관리 - 회사 정보를 수정합니다.");
        sender.sendMessage("/company 도움말 - 도움말을 표시합니다.");
        sender.sendMessage("/company 리스트 - 회사 목록을 표시합니다.");
        sender.sendMessage("/company 주식시장 [켜기/끄기] - 회사 주식의 활성화 여부를 결정합니다.");
        sender.sendMessage("/company 주식요일제 [7자리의 2진수] - 회사 주식의 활성화 요일을 결정합니다.");
        sender.sendMessage("/company 일일업무 - 회사에서 오늘의 업무를 받아옵니다.");
        sender.sendMessage("/company 입사목록 - 회사에 입사를 신청한 유저의 목록을 불러옵니다.");
        sender.sendMessage("/company 입사신청 [회사이름] - 입력한 회사에 입사를 신청합니다.");
    }
}
