import com.team573.gongguri.domain.member.entity.Member
import com.team573.gongguri.domain.member.entity.Univ

object MemberFixture {

	fun createWithId(memberId: Long): Member {
		return Member(
			memberId = memberId,
			nickname = "test1",
			univ = Univ("공구리대학교"),
			email = "test1@test.com",
			password = "123123"
		)
	}
}