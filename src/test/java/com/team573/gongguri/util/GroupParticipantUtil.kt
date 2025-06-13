package com.team573.gongguri.util

import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchase
import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchaseParticipant
import com.team573.gongguri.domain.grouppurchase.entity.ParticipationStatus
import com.team573.gongguri.domain.member.entity.Member

object GroupParticipantUtil {
	fun createWithId(id: Long, member: Member, groupPurchase: GroupPurchase): GroupPurchaseParticipant {
		return GroupPurchaseParticipant(
			groupParticipantId = id,
			groupPurchase = groupPurchase,
			member = member,
			participationStatus = ParticipationStatus.JOINED
		)
	}

	fun createList(): MutableList<GroupPurchaseParticipant> {
		val list: MutableList<GroupPurchaseParticipant> = mutableListOf()

		for (i in 1..10) {
			val id = i.toLong()
			val member = MemberUtil.createWithId(id)
			val chatRoom = ChatRoomUtil.createWithId(1)

			val groupPurchase = GroupPurchaseUtil.createWithId(id, member, chatRoom)
			list.add(createWithId(id, member, groupPurchase))
		}

		return list
	}

	fun createList(member: Member): MutableList<GroupPurchaseParticipant> {
		val list: MutableList<GroupPurchaseParticipant> = mutableListOf()

		for (i in 1..10) {
			val id = i.toLong()
			val chatRoom = ChatRoomUtil.createWithId(1)

			val groupPurchase = GroupPurchaseUtil.createWithId(id, member, chatRoom)
			list.add(createWithId(id, member, groupPurchase))
		}

		return list
	}
}