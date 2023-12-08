package com.example.hotelcheckin

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import kotlinx.coroutines.runBlocking
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import java.nio.charset.StandardCharsets
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.sign
import kotlin.system.exitProcess

fun today(): Int {
    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    val result = current.format(formatter).toInt()

    return result
}

fun main() {
    val reserveList = ArrayList<ReserveList>()
    var peopleList = ArrayList<People>()
    println("호텔예약 프로그램 입니다.")
    while (true) {
        var cash = ThreadLocalRandom.current().nextInt(10000, 50000)
        var banlance = cash
        println("[메뉴]")
        println("1. 방예약, 2. 예약목록 출력, 3.예약목록 (정렬) 출력, 4. 시스템 종료, 5. 금액 입금-출금 내역 목록 출력 6. 예약 변경/취소 7. 영수증 출력 8. 영수증 확인")
        var num = readln().toInt()
        when (num) {
            1 -> {
                var a = reserve(reserveList, cash, banlance)
                if (a != null) {
                    if (a.price < cash) {
                        println("호텔 예약이 완료되었습니다")
                        reserveList.add(a)
                        a.people.balance -= a.price

                        peopleList.add(a.people)
                    } else {
                        println("금액이 부족합니다. 예약이 취소되었습니다")
                    }
                }
            }

            2 -> {
                for (i in reserveList) {
                    println("사용자: ${i.people.name}, 방번호: ${i.roomnum}, 체크인: ${i.checkin}. 체크아웃: ${i.checkout}. 금액: ${i.price} 원")
                }
            }

            3 -> {
                reserveList.sortBy { it.checkin }
                for (i in reserveList) {
                    println("사용자: ${i.people.name}, 방번호: ${i.roomnum}, 체크인: ${i.checkin}. 체크아웃: ${i.checkout}. 금액: ${i.price} 원")
                }
            }

            4 -> exitProcess(0)
            5 -> {
                println("조회하실 사용자 이름을 입력하세요")
                val a = readln()
                var sumcash = 0
                var sumprice = 0
                var sumbal = 0
                with(peopleList.find { it.name == a }) {
                    if (this == null) {
                        println("예약된 사용자를 찾을 수 없습니다.")
                    } else {
                        for (i in peopleList) {
                            if (i.name == this.name) {
                                sumcash += i.cash
                                sumprice += i.price
                                sumbal += i.balance
                            }
                        }
                        println("${this.name}")
                        println("1. 초기 금액으로 ${sumcash} 원 입금되었습니다.")
                        println("2. 예약금으로 ${sumprice}원 출금되었습니다.")
                        println("3. 잔액이 ${sumbal}원 남았습니다.")
                    }
                }
            }

            6 -> {
                println("예약을 변경한 사용자 이름을 입력하세요")
                val a = readln()
                with(reserveList.filter { it.people.name == a }) {
                    if (this.isEmpty()) {
                        println("예약된 사용자를 찾을 수 없습니다.")
                    } else {
                        while (true) {
                            println("$a 님이 예약한 목록입니다. 변경하실 예약번호를 입력해주세요 (탈출은 exit입력)")
                            for (i in 0 until this.size) {
                                var q = this[i]
                                println("${i + 1}. 방번호: ${q.roomnum}, 체크인: ${q.checkin}. 체크아웃: ${q.checkout}")
                            }
                            var a = readln()

                            var b = a.toInt()
                            if (a == "exit") {
                                break
                            } else {
                                when (b) {
                                    in 1..this.size -> {
                                        var item = this[b - 1]
                                        println("해당 예약을 어떻게 하시겠어요 1. 변경 2. 취소 / 이외 번호. 메뉴 돌아가기")
                                        var a = readln().toInt()
                                        when (a) {
                                            1 -> {
                                                var roomnum = 0
                                                while (true) {
                                                    println("예약할 방번호를 입력해주세요")
                                                    roomnum = readln().toInt()
                                                    if (roomnum in (100..999)) break

                                                    System.err.println("올바르지 않은 방번호 입니다. 방번호는 100~999 영역 이내입니다.")
                                                }
                                                val today = today()
                                                var checkin = 0
                                                while (true) {
                                                    println("체크인 날짜를 입력해주세요 표기형식. 20230631")
                                                    checkin = readln().toInt()
                                                    if (checkin.toString().length != 8) {
                                                        println("표기형식에 맞춰 주세요")
                                                        continue
                                                    }
                                                    val checkinDate = dateform(checkin)
                                                    if (!overlapCheck(
                                                            roomnum,
                                                            checkinDate,
                                                            reserveList
                                                        )
                                                    ) {
                                                        continue
                                                    }
                                                    if (checkin >= today) break

                                                    println("체크인은 지난날을 선택할 수 없습니다.")
                                                }
                                                var checkout = 0
                                                while (true) {
                                                    println("체크아웃 날짜를 입력해주세요 표기형식. 20230631")
                                                    checkout = readln().toInt()
                                                    val chekoutDate = dateform(checkout)
                                                    if (!overlapCheck(
                                                            roomnum,
                                                            chekoutDate,
                                                            reserveList
                                                        )
                                                    ) {
                                                        continue
                                                    }
                                                    if (checkout > checkin) break

                                                    println("체크인 날짜보다 이전이거나 같을 수는 없습니다")
                                                }

                                                this[a - 1].roomnum = roomnum
                                                this[a - 1].checkin = dateform(checkin)
                                                this[a - 1].checkout = dateform(checkout)
                                                println("예약 변경이 완료되었습니다.")
                                                //룸넘버부터 체크아웃까지 하고 기존거와 교체
                                                break
                                            }

                                            2 -> {
                                                println("[취소 유의사항]")
                                                println("체크인 3일 이전 취소 예약금 환불 불가")
                                                println("체크인 5일 이전 취소 예약금의 30% 환불")
                                                println("체크인 7일 이전 취소 예약금의 50% 환불")
                                                println("체크인 14일 이전 취소 예약금의 80% 환불")
                                                println("체크인 30일 이전 취소 예약금의 100% 환불")
                                                val diff = Duration.between(
                                                    LocalDate.now().atStartOfDay(),
                                                    item.checkin.atStartOfDay()
                                                )
                                                val diffDays = diff.toDays().toInt()
                                                val refund = if (diffDays <= 3) {
                                                    (item.price * 0)
                                                } else if (diffDays <= 5) {
                                                    (item.price * 0.30)
                                                } else if (diffDays <= 7) {
                                                    (item.price * 0.50)
                                                } else if (diffDays <= 14) {
                                                    (item.price * 0.80)
                                                } else {
                                                    item.price
                                                }
                                                for (i in peopleList) {
                                                    if (i.name == item.people.name && i.price == item.price) {
                                                        i.balance = refund.toInt()
                                                        break
                                                    }

                                                }
                                                reserveList.remove(item)
                                                println("취소가 완료되었습니다")
                                                break
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            7 -> {
                println("영수증 내보내실 분의 성함을 입력해주세요")
                val name = readln()
                val filename = "C:\\Users\\zaq86\\OneDrive\\바탕 화면\\Velogimg\\$name 님 예약 영수증.csv"
                val sortedArray = reserveList.sortedBy { item -> item.people.name }
                try {

                    File(filename).printWriter().use { out ->
                        //헤더 작성
                        out.println("Name, RoomNumber, CheckIn, CheckOut, Price")

                        sortedArray.forEach { it ->
                            out.println("${it.people.name},${it.roomnum},${it.checkin},${it.checkout},${it.price}")
                        }
                    }

                    println("영수증이 성공적으로 내보내졌습니다. 파일 경로: $filename")
                }catch (e: Exception) {
                    println("에러 발생: $e")
                }
            }


            else -> println("잘못된입력입니다")
        }
    }
}

fun reserve(reserveList: ArrayList<ReserveList>, cash: Int, balance: Int): ReserveList {
    val reserveList = reserveList
    println("예약자분의 성함을 입력해주세요")
    val name = readln().toString()
    var roomnum = 0
    while (true) {
        println("예약할 방번호를 입력해주세요")
        roomnum = readln().toInt()
        if (roomnum in (100..999)) break

        System.err.println("올바르지 않은 방번호 입니다. 방번호는 100~999 영역 이내입니다.")
    }
    val today = today()
    var checkin = 0
    while (true) {
        println("체크인 날짜를 입력해주세요 표기형식. 20230631")
        checkin = readln().toInt()
        if (checkin.toString().length != 8) {
            println("표기형식에 맞춰 주세요")
            continue
        }
        val checkinDate = dateform(checkin)
        if (overlapCheck(roomnum, checkinDate, reserveList) == false) {
            continue
        }
        if (checkin >= today) break

        println("체크인은 지난날을 선택할 수 없습니다.")
    }
    var checkout = 0
    while (true) {
        println("체크아웃 날짜를 입력해주세요 표기형식. 20230631")
        checkout = readln().toInt()
        val chekoutDate = dateform(checkout)
        if (overlapCheck(roomnum, chekoutDate, reserveList) == false) {
            continue
        }
        if (checkout > checkin) break

        println("체크인 날짜보다 이전이거나 같을 수는 없습니다")
    }
    val checkinDate = dateform(checkin)
    val checkoutDate = dateform(checkout)
    val price = ThreadLocalRandom.current().nextInt(5000, 30000)
    val who = People(name, cash, balance, price)
    val reservation = ReserveList(
        people = who,
        roomnum = roomnum,
        checkin = checkinDate,
        checkout = checkoutDate,
        price
    )
    return reservation
}

fun dateform(a: Int): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    val x = LocalDate.parse(a.toString(), formatter)
    return LocalDate.parse(x.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
}

fun overlapCheck(
    roomnum: Int,
    chekinDate: LocalDate,
    reserveList: ArrayList<ReserveList>
): Boolean {
    for (i in reserveList) {
        if (i.roomnum == roomnum && chekinDate.isBefore(i.checkout) && chekinDate.isAfter(i.checkin)) {
            println("해당 날짜에 이미 방을 사용중입니다. 다른날짜를 입력해주세요")
            return false
        } else if (i.roomnum == roomnum && chekinDate == i.checkout) {
            println("해당 날짜에 이미 방을 사용중입니다. 다른날짜를 입력해주세요")
            return false
        } else if (i.roomnum == roomnum && chekinDate == i.checkin) {
            println("해당 날짜에 이미 방을 사용중입니다. 다른날짜를 입력해주세요")
            return false
        }
    }
    return true
}
//fun readCsv(inputStream: InputStream): List<ReserveList> {
//    val reader = inputStream.bufferedReader()
//    val header = reader.readLine()
//    return reader.lineSequence().filter { it.isNotBlank() }.map {
//        val (people, roomnum, checkin, checkout, price) = it.split()
//    }
//}

