package playhub;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Community extends Observable {

	private String communityId;
	private String name;
	private String sportType;
	private String description;
	private Date createdDate;
	private final List<User> memberList = new ArrayList<>();
	private final List<Session> sessionList = new ArrayList<>();

	public Community(String name, String sportType, String description, Date createdDate) {
		this.communityId = UUID.randomUUID().toString();
		this.name = name;
		this.sportType = sportType;
		this.description = description;
		this.createdDate = createdDate;
	}

	public void addMember(User user) {
		if (!memberList.contains(user)) {
			memberList.add(user);
			user.joinCommunity(this);
			attach(user);
			notify("New member joined: " + user.getName());
		}
	}

	public void removeMember(User user) {
		if (user == null || !memberList.contains(user)) {
			return;
		}
		memberList.remove(user);
		user.leaveCommunity(this);
		detach(user);
		notify("Member left: " + user.getName());
	}

	public List<User> getCommunityMember() {
		return new ArrayList<>(memberList);
	}

	public void addSession(Session sessionObj) {
		sessionList.add(sessionObj);
		notify("New session added: " + sessionObj.getTitle());
	}

	public void notifyAllMembers(String message) {
		notify(message);
	}

	public Community findCommunity(String communityId) {
		if (this.communityId.equals(communityId)) {
			return this;
		}
		return null;
	}

	public String getCommunityId() {
		return communityId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSportType() {
		return sportType;
	}

	public void setSportType(String sportType) {
		this.sportType = sportType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public List<Session> getSessionList() {
		return new ArrayList<>(sessionList);
	}

	public int getMemberCount() {
		return memberList.size();
	}

}
