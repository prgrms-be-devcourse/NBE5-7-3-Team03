package com.team573.gongguri.util

import com.team573.gongguri.domain.chat.entity.ChatRoom
import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchase
import com.team573.gongguri.domain.grouppurchase.entity.ProgressStatus
import com.team573.gongguri.domain.member.entity.Member
import com.team573.gongguri.domain.member.entity.Univ

object GroupPurchaseUtil {
	fun createWithId(id: Long, member: Member, chatRoom: ChatRoom): GroupPurchase {
		return GroupPurchase(
			groupId = id,
			member =  member,
			univ = Univ("공구리대학교"),
			chatRoom = chatRoom,
			progressStatus = ProgressStatus.RECRUITING,
			title = "공구리 공구",
			content = "쌉니다 싸요",
			price = 10000,
			maxParticipants = 100,
			bank = "여간기합은행",
			account = "1234567890",
			imageUrl = "image/jpeg"
		)
	}

	fun createList(member: Member): List<GroupPurchase> {
		val purchaseList = mutableListOf<GroupPurchase>()
		for(i in 1L..10L) {
			purchaseList.add(
				createWithId(i, member, ChatRoomUtil.createWithId(i))
			)
		}
		return purchaseList
	}
}