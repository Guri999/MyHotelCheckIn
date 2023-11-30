package com.example.hotelcheckin

import android.util.Log
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess

fun today():Int{
    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    val result = current.format(formatter).toInt()

    return result
}

fun main(){
    println("호텔예약 프로그램 입니다.")
    println("[메뉴]")
    println("1. 방예약, 2. 예약목록 출력, 3.예약목록 (정렬) 출력, 4. 시스템 종료, 5. 금액 입금-출금 내역 목록 출력 6. 예약 변경/취소")
    var num = readln().toInt()
    when(num){
        1-> reserve()
        4-> exitProcess(0)

        else -> println("잘못된입력입니다")
    }
}
fun reserve(){
    println("예약자분의 성함을 입력해주세요")
    val name = readln().toString()
    while (true) {
        println("예약할 방번호를 입력해주세요")
        var roomnum = readln().toInt()
        if (roomnum in (100..999))break

        println("\u001B[31m올바르지 않은 방번호 입니다. 방번호는 100~999 영역 이내입니다.\u001B[0m")
    }
    val today = today()
    var checkin = 0
    while (true) {
        println("체크인 날짜를 입력해주세요 표기형식. 20230631")
        checkin = readln().toInt()
        if (checkin >= today ) break

        println("체크인은 지난날을 선택할 수 없습니다.")
    }
    var checkout = 0
    while (true){
        println("체크아웃 날짜를 입력해주세요 표기형식. 20230631")
        checkout = readln().toInt()
        if (checkout > checkin) break

        println("체크인 날짜보다 이전이거나 같을 수는 없습니다")
    }
    println("호텔 예약이 완료되었습니다")
}