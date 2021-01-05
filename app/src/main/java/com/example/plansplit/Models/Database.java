package com.example.plansplit.Models;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.plansplit.Controllers.Adapters.AddGroupsAdapter;
import com.example.plansplit.Controllers.Adapters.GroupAdapter;
import com.example.plansplit.Models.Objects.Expense;
import com.example.plansplit.Models.Objects.Friend;
import com.example.plansplit.Models.Objects.FriendRequest;
import com.example.plansplit.Models.Objects.Groups;
import com.example.plansplit.Models.Objects.Notification;
import com.example.plansplit.Models.Objects.Person;
import com.example.plansplit.Models.Objects.ToDoList;
import com.example.plansplit.Models.Objects.Transfers;
import com.example.plansplit.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class Database {

    //error list for easy debugging
    private static final String DATABASE_ERROR = "DATABASE_ERROR";
    private static final String KEY_NOT_FOUND = "KEY_NOT_FOUND";
    private static final String FRIEND_LIST_EMPTY = "FRIEND_LIST_EMPTY";
    private static final String VALUE_NOT_FOUND = "VALUE_NOT_FOUND";
    private static final String USER_AND_TARGET_KEY_SAME = "USER_AND_TARGET_KEY_SAME";
    private static final String ALREADY_FRIENDS = "ALREADY_FRIENDS";
    private static final String ALREADY_SENT_FRIEND_REQUEST = "ALREADY_SENT_FRIEND_REQUEST";
    private static final String NO_SELECTED_FRIEND = "NO_SELECTED_FRIEND";
    private static final String NO_GIVEN_GROUP_NAME = "NO_GIVEN_GROUP_NAME";
    private static final String ALREADY_IN_GROUP = "ALREADY_IN_GROUP";
    private static final String ALREADY_REMOVED_FROM_GROUP = "ALREADY_REMOVED_FROM_GROUP";

    //Firebase
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference user_reference = database.getReference("users");
    private static final DatabaseReference friend_reference = database.getReference("friends");
    private static final DatabaseReference group_reference = database.getReference("groups");

    private static final String TAG = "DATABASE";
    private static Person person;

    public Person getPerson(){ return person; }

    //Singleton
    public static Database getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final Database INSTANCE = new Database();
    }

    /**
     * Creates a child with "key" under user reference. No controls are made, so before use it
     * parameters should be checked
     *
     * @param key       Google ID
     * @param name      display name of google account
     * @param email     current email of google account
     * @param image     profile photo of google account
     * @param date      last login date (millis from EPOCH 01.01.1970 00:00)
     */
    public void registerUser(String key, String name, String email, String image, long date) {
        user_reference.child(key).child("name").setValue(name);
        user_reference.child(key).child("email").setValue(email);
        user_reference.child(key).child("image").setValue(image);
        user_reference.child(key).child("last_login").setValue(date);
        createPerson(key, name, email, image, System.currentTimeMillis());
    }

    /**
     *  Creates a static Person object to fast access to user's info
     * @param key       Google ID, also used in database
     * @param name      display name of google account
     * @param email     current email of google account
     * @param image     profile photo of google account
     * @param date      last login date (millis from EPOCH 01.01.1970 00:00)
     */
    private void createPerson(String key, String name, String email, String image, long date){
        person = new Person(key, name, email, image, date);
    }

    public interface ExpenseCallBack {
        void onExpenseRetrieveSuccess(ArrayList<Expense> expenses);
        void onError(String error_tag, String error);
    }

    public interface BudgetCallBack {
        void onBudgetRetrieveSuccess(int budget);
        void onError(String error_tag, String error);
    }

    /**
     * Interface for receiving a Friend object.
     * One method with successfully retrieving the object, other one for errors
     */
    public interface FriendCallBack {
        void onFriendRetrieveSuccess(Friend friend);

        void onError(String error_tag, String error);
    }

    public interface ToDoListCallBack {
        void onToDoListRetrieveSuccess(ToDoList todo);
        void onError(String error_tag, String error);
    }

    /**
     * Interface for receiving a FriendRequest object.
     * One method with successfully retrieving the object, other one for errors
     */
    public interface FriendRequestCallBack {
        void onFriendRequestRetrieveSuccess(FriendRequest friend_request);
        void onError(String error_tag, String error);
    }

    /**
     * Standard interface for status report from Database operations
     */
    public interface DatabaseCallBack {
        void onSuccess(String success);
        void onError(String error_tag, String error);
    }

    public interface GetMemberInfoCallBack {
        void onGetMemberInfoRetrieveSuccess(ArrayList<Friend> members);
        void onError(String error_tag, String error);
    }

    public interface getDebtFromFriendCallBack {
        void onGetDebtFromFriendRetrieveSuccess(float debt);
        void onError(String error_tag, String error);
    }

    public interface getDebtFromGroupCallBack {
        void onGetDebtFromGroupRetrieveSuccess(float debt);
        void onError(String error_tag, String error);
    }

    public interface GroupCallBack {
        void onGroupRetrieveSuccess(Groups selected_group);
        void onError(String error_tag, String error);
    }

    public interface NotificationCallBack {
        void onNotificationsRetrieveSuccess(ArrayList<Notification> notifications);
        void onError(String error_tag, String error);
    }

    public interface TransferCallBack {
        void onTransferRetrieveSuccess(ArrayList<Transfers> transfers);
        void onError(String error_tag, String error);
    }

    public interface LoginDateCallBack {
        void onLoginDateRetrieveSuccess(long date);
        void onError(String error_tag, String error);
    }

    /**
     * A private handler for adding new friend, removing request and removing friends node on error
     */
    @SuppressWarnings("unused")
    private interface AddFriendHandler {
        void addAsFriend(String key, ArrayList<String> friends);

        void removeFromRequests(String key, ArrayList<String> friend_reqs);

        void removeFriendsOnError(String friend_list_key);
    }

    /**
     * A private handler for adding new friend request
     */
    @SuppressWarnings("unused")
    private interface SendFriendRequestHandler {
        void addToRequests(String key, ArrayList<String> friend_reqs);
    }

    /**
     * A private handler for declining the friend request
     */
    @SuppressWarnings("unused")
    private interface DeclineFriendRequestHandler {
        void declineFriendRequest(String key, ArrayList<String> friend_reqs);
    }

    /**
     * A private handler for removing friend from friend list and deleting friend snapshot
     */
    @SuppressWarnings("unused")
    private interface RemoveFriendHandler {
        void removeFriend(String key);

        void removeFromFriendList(String key, ArrayList<String> friends);
    }

    /**
     * A private handler for adding new ToDos
     */
    @SuppressWarnings("unused")
    private interface ToDoListHandler {
        void handler(String key);
    }

    @SuppressWarnings("unused")
    private interface NotificationChangeHandler {
        void handler(String key);
    }

    @SuppressWarnings("unused")
    private interface DatabaseNotificationListener {
        void onPersonalEvent(String type, String whom, String image, String info);

        void onFriendEvent(String type, String whom, String image, String info);

        void onGroupEvent(String type, String whom, String image, String info);
    }

    @SuppressWarnings("unused")
    private interface MemberInfoHandler {
        void handler(Friend friend);
    }

    /**
     *  Returns user's last login as long
     * @param callBack callback for returning last login
     */
    public void getLastLogin(final LoginDateCallBack callBack){
        user_reference.child(person.getKey()).child("last_login").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    callBack.onError(VALUE_NOT_FOUND, "son giriş bulunamadı");
                    return;
                }
                long date = (long) snapshot.getValue();
                callBack.onLoginDateRetrieveSuccess(date);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    /**
     * Listener interface for notification handling
     */
    private final DatabaseNotificationListener notificationListener = new DatabaseNotificationListener() {
        /**
         * Creates a Notification, with current time (in millis as long)
         * @param type  type of event, types can be found in Notification's Constructor
         * @param whom  id of the user, whom the event will be saved
         * @param image related image of info
         * @param info  info, can be name or amount of expense etc.
         * @see Notification
         */
        @Override
        public void onPersonalEvent(String type, String whom, String image, String info) {
            long date = System.currentTimeMillis();
            DatabaseReference not_ref= user_reference.child(whom).child("notifications").push();
            not_ref.child("type").setValue(type);
            not_ref.child("date").setValue(date);
            not_ref.child("image").setValue(image);
            not_ref.child("info").setValue(info);
        }

        @Override
        public void onFriendEvent(String type, String whom, String image, String info) { }

        @Override
        public void onGroupEvent(String type, String whom, String image, String info) { }
    };

    /**
     * interface method for notification creation outside of Database
     */
    public void createNotification(@NotNull String prime_type,
                                   String whom,
                                   String type,
                                   String image,
                                   String info,
                                   DatabaseCallBack callBack){
        switch (prime_type) {
            case "personal":
                notificationListener.onPersonalEvent(type, whom, image, info);
                break;
            case "friend":
                notificationListener.onFriendEvent(type, whom, image, info);
                break;
            case "group":
                notificationListener.onGroupEvent(type, whom, image, info);
                break;
            default:
                callBack.onError("WRONG_TYPE", "verilen üst-tür bulunamadı");
                return;
        }
        callBack.onSuccess("notification oluşturuldu");
    }

    /**
     * Would be used for for example updating "over_budget" notification.
     */
    public void changeNotification(String key_type, String key, final String searched_type, final String info, final DatabaseCallBack callBack){
        final DatabaseReference ref;
        switch (key_type){
            case "personal":
                ref = database.getReference("users").child(key);
                break;
            case "friend":
                ref = database.getReference("friends").child(key);
                break;
            case "group":
                ref = database.getReference("groups").child(key);
                break;
            default:
                callBack.onError(VALUE_NOT_FOUND, "yanlış key tipi");
                return;
        }
        final Database.NotificationChangeHandler handler = new NotificationChangeHandler() {
            @Override
            public void handler(String key) {
                long date = System.currentTimeMillis();
                ref.child("notifications").child(key).child("info").setValue(info);
                ref.child("notifications").child(key).child("date").setValue(date);
            }
        };
        ref.child("notifications").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    callBack.onError(VALUE_NOT_FOUND, "notifications bulunamadı");
                    return;
                }
                @SuppressWarnings("unchecked")
                HashMap<String, String> notifications = (HashMap<String, String>) snapshot.getValue();
                for (String not_key: notifications.keySet()){
                    DataSnapshot ss = snapshot.child(not_key);
                    String type = (String) ss.child("type").getValue();
                    if (searched_type.equals(type)){
                        callBack.onSuccess("notification bulundu");
                        String old_info = (String) ss.child("info").getValue();
                        if (info.equals(old_info)){
                            return;
                        }
                        handler.handler(ss.getKey());
                        return;
                    }
                }
                callBack.onError(VALUE_NOT_FOUND, "notifications bulunamadı");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    /**
     *  checks if given type of notification exists in given reference. <b>
     *  For Example "over_budget" notification. If already exists, it should be updated, not re-sent
     * @param key_type  personal, friend or group
     * @param key   user_key, friendship_key or group_key
     * @param searched_type searched type for this specific "key_type"
     * @param callBack  standard callBAck, if found onSuccess, else calls onError
     */
    public void checkNotificationExists(String key_type, String key, final String searched_type, final DatabaseCallBack callBack){
        DatabaseReference ref;
        switch (key_type){
            case "personal":
                ref = database.getReference("users").child(key);
                break;
            case "friend":
                ref = database.getReference("friends").child(key);
                break;
            case "group":
                ref = database.getReference("groups").child(key);
                break;
            default:
                callBack.onError(VALUE_NOT_FOUND, "yanlış key tipi");
                return;
        }
        ref.child("notifications").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    callBack.onError(VALUE_NOT_FOUND, "notifications bulunamadı");
                    return;
                }
                @SuppressWarnings("unchecked")
                HashMap<String, String> notifications = (HashMap<String, String>) snapshot.getValue();
                for (String not_key: notifications.keySet()){
                    DataSnapshot ss = snapshot.child(not_key);
                    String type = (String) ss.child("type").getValue();
                    if (searched_type.equals(type)){
                        callBack.onSuccess("notification bulundu");
                        return;
                    }
                }
                callBack.onError(VALUE_NOT_FOUND, "notifications bulunamadı");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    /**
     * deletes all notifications from given keys reference. <b>
     * For Example monthly resetting.
     */
    public void deleteAllNotifications(String key_type, String key, DatabaseCallBack callBack){
        switch (key_type){
            case "personal":
                user_reference.child(key).child("notifications").setValue(null);
                break;
            case "friend":
                friend_reference.child(key).child("notifications").setValue(null);
                break;
            case "group":
                group_reference.child(key).child("notifications").setValue(null);
                break;
            default:
                callBack.onError(VALUE_NOT_FOUND, "yanlış key tipi");
                return;
        }
        callBack.onSuccess("notifications removed");
    }

    /**
     * Returns all personal notification of user as ArrayList
     * @param context needed for notification creation, in order to get res data from app for
     *                specific info
     * @param callBack returns ArrayList of Notifications on onSuccess
     */
    public void getPersonalNotifications(final Context context, final NotificationCallBack callBack){
        user_reference.child(person.getKey()).child("notifications").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    callBack.onError(VALUE_NOT_FOUND, "notifications boş");
                    return;
                }
                ArrayList<Notification> notifications = new ArrayList<>();
                @SuppressWarnings("unchecked")
                HashMap<String, String> notification_keys = (HashMap<String, String>) snapshot.getValue();
                for (String not_key: notification_keys.keySet()){
                    DataSnapshot not_snapshot = snapshot.child(not_key);
                    String type = not_snapshot.child("type").getValue().toString();
                    long date = (Long) not_snapshot.child("date").getValue();
                    String image = not_snapshot.child("image").getValue().toString();
                    String info = not_snapshot.child("info").getValue().toString();
                    notifications.add(new Notification(context, type, date, image, info));
                }
                callBack.onNotificationsRetrieveSuccess(notifications);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    public void deleteAllPersonalExpenses(DatabaseCallBack callBack){
        user_reference.child(person.getKey()).child("expenses").setValue(null);
        callBack.onSuccess("harcamalar silindi");
    }

    /**
     * Updates are made for the friends section that will take place in the todolist.
     */
    public void updateDoListFriend(final String friend_key, final String toDo_key, final String operation, final DatabaseCallBack callBack) {
        final ToDoListHandler handler = new ToDoListHandler() {
            @Override
            public void handler(String key){
                if(operation.equals("save")){
                    friend_reference.child(key).child("todos").child(toDo_key).child("resp_person_name").setValue(person.getName());
                    friend_reference.child(key).child("todos").child(toDo_key).child("resp_person").setValue(person.getKey());
                    friend_reference.child(key).child("todos").child(toDo_key).child("status").setValue("reserved");
                } else if (operation.equals("cancel")) {
                    friend_reference.child(key).child("todos").child(toDo_key).child("resp_person_name").setValue("none");
                    friend_reference.child(key).child("todos").child(toDo_key).child("resp_person").setValue("none");
                    friend_reference.child(key).child("todos").child(toDo_key).child("status").setValue("waiting");
                } else if (operation.equals("delete")) {
                    friend_reference.child(key).child("todos").child(toDo_key).setValue(null);
                }
            }
        };
        //The information of the user making the transaction is taken.
        user_reference.child(person.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot_user) {
                if (!snapshot_user.exists()) {
                    callBack.onError(KEY_NOT_FOUND,
                            person.getKey() + " ile ilişkili kullanıcı bulunamadı");
                    return;
                }
                @SuppressWarnings("unchecked")
                ArrayList<String> friend_list_keys = (ArrayList<String>)
                        snapshot_user.child("friends").getValue();            //kullanıcı arkadaş listeleri Mli olanlar
                if (friend_list_keys == null) {
                    callBack.onError(FRIEND_LIST_EMPTY, "Arkadaş listesi boş");
                    return;
                }
                for (final String friend_list_key : friend_list_keys) {
                    friend_reference.child(friend_list_key).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                callBack.onError(VALUE_NOT_FOUND, "friends snapshot'ında arkadaş keyleri yok");
                                return;
                            }
                            @SuppressWarnings("unchecked")
                            ArrayList<String> friends = (ArrayList<String>) snapshot.getValue();
                            for (String key : friends) {
                                if (friend_key.equals(key)) {
                                    handler.handler(friend_list_key);
                                    callBack.onSuccess("success");
                                    return;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            callBack.onError(DATABASE_ERROR, error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    /**
     * Updates are made for the groups section that will take place in the todolist.
     */
    public void updateToDoListGroup(@NonNull final String group_key, @NonNull final String toDo_key, final String operation, final DatabaseCallBack callBack){
        if(operation.equals("save")){
            group_reference.child(group_key).child("todos").child(toDo_key).child("resp_person_name").setValue(person.getName());
            group_reference.child(group_key).child("todos").child(toDo_key).child("resp_person").setValue(person.getKey());
            group_reference.child(group_key).child("todos").child(toDo_key).child("status").setValue("reserved");
        } else if (operation.equals("cancel")) {
            group_reference.child(group_key).child("todos").child(toDo_key).child("resp_person_name").setValue("none");
            group_reference.child(group_key).child("todos").child(toDo_key).child("resp_person").setValue("none");
            group_reference.child(group_key).child("todos").child(toDo_key).child("status").setValue("waiting");
        } else if (operation.equals("delete")) {
            group_reference.child(group_key).child("todos").child(toDo_key).setValue(null);
        }
        callBack.onSuccess("group todo list updatelendi: " + operation);
    }

    /**
     * Information on todolists present in the friends section is obtained.
     */
    public void getToDoListFriend(final String friend_key, final ToDoListCallBack callBack){
        user_reference.child(person.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot_user) {
                if (!snapshot_user.exists()) {
                    callBack.onError(KEY_NOT_FOUND, person.getKey() + " ile ilişkili kullanıcı bulunamadı");
                    return;
                }
                @SuppressWarnings("unchecked")
                ArrayList<String> friend_list_keys = (ArrayList<String>)
                        snapshot_user.child("friends").getValue();
                if (friend_list_keys == null) {
                    callBack.onError(FRIEND_LIST_EMPTY, "Arkadaş listesi boş");
                    return;
                }
                for (final String friend_list_key : friend_list_keys) {
                    //assuming snapshot with friend_list_key exists, if not throws Exception
                    friend_reference.child(friend_list_key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot snapshot_friend) {
                            if (!snapshot_friend.exists()) {
                                callBack.onError(VALUE_NOT_FOUND, "friends snapshot'ında todo yok");
                                return;
                            }
                            @SuppressWarnings("unchecked")
                            ArrayList<String> friends = (ArrayList<String>) snapshot_friend.child("friends").getValue();
                            for (String key : friends) {
                                if (friend_key.equals(key)) {

                                    @SuppressWarnings("unchecked")
                                    HashMap<String, String> todos = (HashMap<String, String>) snapshot_friend.child("todos").getValue();
                                    if (todos == null) {
                                        callBack.onError(VALUE_NOT_FOUND, "todos boş");
                                        return;
                                    }
                                    for (final String key2 : todos.keySet()) {
                                        final DataSnapshot snapshotTodo = snapshot_friend.child("todos").child(key2);
                                        final String description = snapshotTodo.child("description").getValue().toString();
                                        final String status = snapshotTodo.child("status").getValue().toString();
                                        final String resp_person_name = snapshotTodo.child("resp_person_name").getValue().toString();
                                        final String who_added = snapshotTodo.child("who_added").getValue().toString();
                                        final String resp_person = snapshotTodo.child("resp_person").getValue().toString();
                                        user_reference.child(who_added).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot_users_friend) {
                                                if (!snapshot_users_friend.exists()) {
                                                    callBack.onError(KEY_NOT_FOUND, "key bulunamadı");
                                                    return;
                                                }
                                                String who_added_name = snapshot_users_friend.child("name").getValue().toString();
                                                ToDoList todo;
                                                if (status.equals("waiting")) {
                                                    todo = new ToDoList(description, who_added_name, resp_person_name, key2, who_added);
                                                } else {
                                                    todo = new ToDoList(description, who_added_name, resp_person_name, resp_person, key2, status, who_added);
                                                }
                                                callBack.onToDoListRetrieveSuccess(todo);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                callBack.onError(DATABASE_ERROR, error.getMessage());
                                            }
                                        });
                                    }
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            callBack.onError(DATABASE_ERROR, error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });

    }

    /**
     * Todos additions are made in the friends section.
     */
    public void addToDoListFriend(final String friend_key, final ToDoList todo, final DatabaseCallBack callBack) {
        final ToDoListHandler handler = new ToDoListHandler() {
            @Override
            public void handler(String key) {
                DatabaseReference databaseReference = friend_reference.child(key).child("todos").push();
                databaseReference.child("description").setValue(todo.getDescription());
                databaseReference.child("resp_person_name").setValue(todo.getResp_person_name());
                databaseReference.child("resp_person").setValue(todo.getResp_person());
                databaseReference.child("status").setValue(todo.getStatus());
                databaseReference.child("who_added").setValue(todo.getWho_added());
            }
        };
    //The information of the user who wants to add the requirement is retrieved.
        user_reference.child(person.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    callBack.onError(KEY_NOT_FOUND,
                            person.getKey() + " ile ilişkili kullanıcı bulunamadı");
                    return;
                }
                @SuppressWarnings("unchecked")
                ArrayList<String> friend_list_keys = (ArrayList<String>)
                        snapshot.child("friends").getValue();
                if (friend_list_keys == null) {
                    callBack.onError(FRIEND_LIST_EMPTY, "Arkadaş listesi boş");
                    return;
                }
                for (final String friend_list_key : friend_list_keys) {
                    //assuming snapshot with friend_list_key exists, if not throws Exception
                    friend_reference.child(friend_list_key).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                callBack.onError(VALUE_NOT_FOUND, "friends snapshot'ında arkadaş keyleri yok");
                                return;
                            }
                            @SuppressWarnings("unchecked")
                            ArrayList<String> friends = (ArrayList<String>) snapshot.getValue();
                            for (String key : friends) {
                                if (friend_key.equals(key)) {
                                    handler.handler(friend_list_key);
                                    callBack.onSuccess("todo eklendi");
                                    return;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            callBack.onError(DATABASE_ERROR, error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    /**
     * Todos additions are made in the groups section.
     */
    public void addToDoListGroup(final String group_key, final ToDoList todo, final DatabaseCallBack callBack) {
        final ToDoListHandler handler = new ToDoListHandler() {
            @Override
            public void handler(String group_key) {
                DatabaseReference todo_ref = group_reference.child(group_key).child("todos").push();
                todo_ref.child("description").setValue(todo.getDescription());
                todo_ref.child("resp_person_name").setValue(todo.getResp_person_name());
                todo_ref.child("resp_person").setValue(todo.getResp_person());
                todo_ref.child("status").setValue(todo.getStatus());
                todo_ref.child("who_added").setValue(todo.getWho_added());
            }
        };
        //The information of the group to which the needs will be added is taken.
        group_reference.child(group_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    callBack.onError(VALUE_NOT_FOUND, "groups snapshot'ında todo yok");
                    return;
                }
                handler.handler(group_key);
                callBack.onSuccess("todo eklendi");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    /**
     * Information on todolists present in the groups section is obtained.
     */
    public void getToDoListGroup(final String group_key, final ToDoListCallBack callBack) {
        group_reference.child(group_key).child("todos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    callBack.onError(VALUE_NOT_FOUND, "groups snapshot'ında todo yok");
                    return;
                }
                @SuppressWarnings("unchecked")
                HashMap<String, String> todos = (HashMap<String, String>) snapshot.getValue();
                if (todos == null) {
                    callBack.onError(VALUE_NOT_FOUND, "todos boş");
                    return;
                }
                for (final String key2 : todos.keySet()) {
                    final DataSnapshot snapshotTodo = snapshot.child(key2);
                    final String description = snapshotTodo.child("description").getValue().toString();
                    final String status = snapshotTodo.child("status").getValue().toString();
                    final String resp_person_name = snapshotTodo.child("resp_person_name").getValue().toString();
                    final String who_added = snapshotTodo.child("who_added").getValue().toString();
                    final String resp_person = snapshotTodo.child("resp_person").getValue().toString();
                    //The information of the user who wants to add the requirement is retrieved.
                    user_reference.child(who_added).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot_users) {
                            if (!snapshot_users.exists()) {
                                callBack.onError(KEY_NOT_FOUND, "key bulunamadı");
                                return;
                            }
                            String who_added_name = snapshot_users.child("name").getValue().toString();
                            ToDoList todo;
                            if (status.equals("waiting")) {
                                todo = new ToDoList(description, who_added_name, resp_person_name, key2, who_added);
                            } else {
                                todo = new ToDoList(description, who_added_name, resp_person_name, resp_person, key2, status, who_added);
                            }
                            callBack.onToDoListRetrieveSuccess(todo);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            callBack.onError(DATABASE_ERROR, error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    /**
     * It searches for all friends of the given keys' user
     * then calls getFriend method to construct the Friend and calls CallBack,
     * which should be specialized
     * <p>
     * ERRORS: <p>
     * {@value DATABASE_ERROR} <p>
     * {@value KEY_NOT_FOUND} <p>
     * {@value FRIEND_LIST_EMPTY} <p>
     *
     * @param user_key unique key whose friends would be searched for
     * @param callBack the callBack to be called whenever an error occurs or task successfully end
     * @see #getFriend(String, String, FriendCallBack)
     */
    public void getFriends(final String user_key, final FriendCallBack callBack) {
        if (user_key == null) {
            callBack.onError(KEY_NOT_FOUND, "kullanıcı keyi null");
            return;
        }
        user_reference.child(user_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    callBack.onError(KEY_NOT_FOUND,
                            user_key + " ile ilişkili kullanıcı bulunamadı");
                    return;
                }
                @SuppressWarnings("unchecked")
                ArrayList<String> friend_list_keys = (ArrayList<String>)
                        snapshot.child("friends").getValue();            //kullanıcı arkadaş listeleri Mli olanlar
                if (friend_list_keys == null) {
                    callBack.onError(FRIEND_LIST_EMPTY, "Arkadaş listesi boş");
                    return;
                }
                for (final String friend_list_key : friend_list_keys) {
                    //assuming snapshot with friend_list_key exists, if not throws Exception
                    friend_reference.child(friend_list_key).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                callBack.onError(VALUE_NOT_FOUND, "friends snapshot'ında arkadaş keyleri yok");
                                return;
                            }
                            @SuppressWarnings("unchecked")
                            ArrayList<String> friends = (ArrayList<String>) snapshot.getValue();
                            for (String key : friends) {
                                if (!user_key.equals(key)) {
                                    getFriend(key, friend_list_key, callBack);
                                    return;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    /**
     * It constructs the Friend object of the given key and send it via FriendCallBack
     * <p>
     * ERRORS: <p>
     * {@value DATABASE_ERROR} <p>
     * {@value KEY_NOT_FOUND} <p>
     *
     * @param friend_key key which would be searched and then constructed as a Friend
     * @param friendshipsKey key which holds all the information between friends
     * @param callBack   the callBack to be called whenever an error occurs or task successfully end
     * @see Friend
     */
    public void getFriend(final String friend_key, final String friendshipsKey, final FriendCallBack callBack) {      //friendshipsKey arkadaşlığın bağlı olduğu friends klasındaki keyi tututyor.
        if (friend_key == null) {
            callBack.onError(KEY_NOT_FOUND, "aranan key null");
            return;
        }
        user_reference.child(friend_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String photo;
                if (!snapshot.exists()) {
                    callBack.onError(KEY_NOT_FOUND, friend_key + " ile ilişkili kullanıcı bulunamadı");
                    return;
                }
                if (!snapshot.child("image").exists()) {
                    photo = "photo yok";
                } else {
                    photo = snapshot.child("image").getValue().toString();
                }

                String name = snapshot.child("name").getValue().toString();
                if (name.equals("No name")) {
                    //if there was one name when user login "No name" would be used, in such case
                    //using email as name would be better to show user to recognize the person
                    name = snapshot.child("email").getValue().toString();
                }
                final Friend friend = new Friend(photo, name, 0, friend_key);
                getDebtFromFriendCallBack friendCallBack = new getDebtFromFriendCallBack() {
                    @Override
                    public void onGetDebtFromFriendRetrieveSuccess(float debt) {
                        friend.setAmount(debt);

                        callBack.onFriendRetrieveSuccess(friend);
                        System.out.println("debt   : " + debt);
                    }

                    @Override
                    public void onError(String error_tag, String error) {

                    }
                };
                friend.setFriendshipsKey(friendshipsKey);
                getDebtFromFriend(person.getKey(), friend, friendCallBack);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    /**
     * It searchs for all friend requests of the given keys' user
     * then calls getFriendRequest method to construct the FriendRequest and calls CallBack,
     * which should be specialized
     * <p>
     * ERRORS: <p>
     * {@value DATABASE_ERROR} <p>
     * {@value KEY_NOT_FOUND} <p>
     * {@value FRIEND_LIST_EMPTY} <p>
     *
     * @param user_key unique key whose friend requests would be searched for
     * @param callBack the callBack to be called whenever an error occurs or task successfully end
     * @see #getFriendRequest(String, FriendRequestCallBack)
     */
    public void getFriendRequests(final String user_key, final FriendRequestCallBack callBack) {
        if (user_key == null) {
            callBack.onError(KEY_NOT_FOUND, "kullanıcı keyi null");
            return;
        }
        user_reference.child(user_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    callBack.onError(KEY_NOT_FOUND,
                            user_key + " ile ilişkili kullanıcı bulunamadı");
                    return;
                }
                @SuppressWarnings("unchecked")
                ArrayList<String> friend_reqs = (ArrayList<String>)
                        snapshot.child("friend_reqs").getValue();
                if (friend_reqs == null) {
                    callBack.onError(FRIEND_LIST_EMPTY, "Arkadaş istekleri listesi boş");
                    return;
                }
                for (final String request_key : friend_reqs) {
                    getFriendRequest(request_key, callBack);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    /**
     * It constructs the FriendRequest object of the given key and send it via FriendRequestCallBack
     * <p>
     * ERRORS: <p>
     * {@value DATABASE_ERROR} <p>
     * {@value KEY_NOT_FOUND} <p>
     *
     * @param request_key key which would be searched and then constructed as a Friend
     * @param callBack    the callBack to be called whenever an error occurs or task successfully end
     * @see FriendRequest
     */
    public void getFriendRequest(final String request_key, final FriendRequestCallBack callBack) {
        if (request_key == null) {
            callBack.onError(KEY_NOT_FOUND, "aranan key null");
            return;
        }
        user_reference.child(request_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    callBack.onError(KEY_NOT_FOUND, request_key + " ile ilişkili kullanıcı bulunamadı");
                    return;
                }
                String image = snapshot.child("image").getValue().toString();
                String name = snapshot.child("name").getValue().toString();
                if (name == null) {
                    //if somehow null is present w/o Exception, use email as name, as it can not be null
                    name = snapshot.child("email").getValue().toString();
                }
                String email = snapshot.child("email").getValue().toString();
                FriendRequest friend_request = new FriendRequest(image, name, email, request_key);
                callBack.onFriendRequestRetrieveSuccess(friend_request);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    /**
     * It searches for given email, if any user is registered with this email,
     * if so, looks if the user and emails owner already friends, already sent request
     * or they are same user. If nor error shows up, calls  searchInFriends, which searches
     * the users friends in friends snapshot, if found calls success, if error shows up,
     * calls searchInFriendRequestList, which searches the users own friend requests list
     * to determine if already a request from the other user sent.
     * If so, rather than sending request, they will be directly added as Friend via addAsFriend method.
     * If not sendFriendRequestInner method will be called to send the requests
     * <p>
     * ERRORS: <p>
     * {@value DATABASE_ERROR} <p>
     * {@value KEY_NOT_FOUND} <p>
     * {@value VALUE_NOT_FOUND} <p>
     * {@value USER_AND_TARGET_KEY_SAME} <p>
     * {@value ALREADY_FRIENDS} <p>
     * {@value ALREADY_SENT_FRIEND_REQUEST} <p>
     *
     * @param user_key key of this user
     * @param email    email of the searched account, whom friend request will be send
     * @param callBack the callBack to be called whenever an error occurs or task successfully end
     * @see #searchInFriends(String, String, DatabaseCallBack)
     * @see #searchInFriendRequestsList(String, String, DatabaseCallBack)
     * @see #addAsFriend(String, String, DatabaseCallBack)
     * @see #sendFriendRequestInner(String, String, DatabaseCallBack)
     */
    public void sendFriendRequest(final String user_key,
                                  final String email,
                                  final DatabaseCallBack callBack) {
        if (user_key == null) {
            callBack.onError(KEY_NOT_FOUND, "kullanıcı keyi null");
            return;
        }
        //to be able to use searchInFriends in other methods DatabaseCallBack must be overridden here
        //in a such way, that it seems wrong. Could not find better solution other than rewrite
        //searchInFriends for each method we need, which is unnecessary
        final DatabaseCallBack innerCallBack = new DatabaseCallBack() {
            @Override
            public void onSuccess(String success) {
                callBack.onError(ALREADY_FRIENDS, "zaten arkadaşlar");
            }

            @Override
            public void onError(String error_tag, String friend_key) {
                searchInFriendRequestsList(user_key, friend_key, callBack);
            }
        };
        user_reference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    callBack.onError(VALUE_NOT_FOUND, email + " ile ilişkili kullanıcı bulunamadı");
                    return;
                }
                //make snapshot usable, which points directly the user with "email"
                snapshot = snapshot.getChildren().iterator().next();
                String friend_key = snapshot.getKey();
                if (friend_key == null) {
                    callBack.onError(KEY_NOT_FOUND, "aranan key null");
                    return;
                }
                if (user_key.equals(friend_key)) {
                    callBack.onError(USER_AND_TARGET_KEY_SAME, email + " kendine istek yollamaya çalışıyor");
                    return;
                }
                @SuppressWarnings("unchecked")
                ArrayList<String> friend_reqs = (ArrayList<String>)
                        snapshot.child("friend_reqs").getValue();
                if (friend_reqs != null) {
                    for (String key : friend_reqs) {
                        if (user_key.equals(key)) {
                            callBack.onError(ALREADY_SENT_FRIEND_REQUEST, "bu emaile daha önce istek yollanmış");
                            return;
                        }
                    }
                }
                @SuppressWarnings("unchecked")
                ArrayList<String> friends = (ArrayList<String>)
                        snapshot.child("friends").getValue();
                if (friends == null) {
                    searchInFriendRequestsList(user_key, friend_key, callBack);
                    return;
                }
                searchInFriends(user_key, friend_key, innerCallBack);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    /**
     * Searches all of the user's friends snapshot to find friend_key, if found it means they
     * are already friends and calls success, if not calls error with friend key for sendFriendRequest
     * <p>
     * ERRORS: <p>
     * {@value DATABASE_ERROR} <p>
     * {@value KEY_NOT_FOUND} <p>
     * {@value VALUE_NOT_FOUND} <p>
     *
     * @param user_key   key of this user
     * @param friend_key key of the other user, who will be searched in friends
     * @param callBack   the callBack to be called whenever an error occurs or task successfully end
     */
    public void searchInFriends(final String user_key,
                                final String friend_key,
                                final DatabaseCallBack callBack) {
        if (user_key == null) {
            callBack.onError(KEY_NOT_FOUND, "kullanıcı keyi null");
            return;
        }
        if (friend_key == null) {
            callBack.onError(KEY_NOT_FOUND, "aranan key null");
            return;
        }
        if (user_key.equals(friend_key)) {
            callBack.onError(USER_AND_TARGET_KEY_SAME, "aranan key null");
            return;
        }
        //assuming there is a user with "user_key", if not throws Exception
        user_reference.child(user_key).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    // although it is not an error message in order to use  this method
                    // in sendFriendRequest, it must send friend_key as error
                    callBack.onError(VALUE_NOT_FOUND, friend_key);
                    return;
                }
                @SuppressWarnings("unchecked")
                ArrayList<String> friend_list_keys = (ArrayList<String>) snapshot.getValue();
                assert friend_list_keys != null;
                final int key_size = friend_list_keys.size();
                int number = 0;
                for (String key : friend_list_keys) {
                    number++;
                    //assuming there is a user with "key", if not throws Exception
                    final int finalNumber = number;
                    friend_reference.child(key).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                callBack.onError(VALUE_NOT_FOUND, friend_key);
                                return;
                            }
                            @SuppressWarnings("unchecked")
                            ArrayList<String> friends = (ArrayList<String>) snapshot.getValue();
                            assert friends != null;
                            for (String key : friends) {
                                if (friend_key.equals(key)) {
                                    String friend_list_key = snapshot.getRef().getParent().getKey();
                                    assert friend_list_key != null;
                                    callBack.onSuccess(friend_list_key);
                                }
                            }
                            if (finalNumber == key_size) {
                                callBack.onError(VALUE_NOT_FOUND, friend_key);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            callBack.onError(DATABASE_ERROR, error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    /**
     * It sends friend request to target account, it is a private method as it does not make controls
     * and should not be used directly, but should be called after control method
     * <p>
     * ERRORS: <p>
     * {@value DATABASE_ERROR} <p>
     * {@value KEY_NOT_FOUND} <p>
     *
     * @param user_key   key of this user
     * @param friend_key key of the other user, whom friend reqeust will be send
     * @param callBack   the callBack to be called whenever an error occurs or task successfully end
     */
    private void sendFriendRequestInner(@NonNull final String user_key,
                                        @NonNull final String friend_key,
                                        @NonNull final DatabaseCallBack callBack) {
        final SendFriendRequestHandler handler = new SendFriendRequestHandler() {
            @Override
            public void addToRequests(String key, ArrayList<String> friend_reqs) {
                user_reference.child(key).child("friend_reqs").setValue(friend_reqs);
                callBack.onSuccess("arkadaşlık isteği gönderildi");
            }
        };
        user_reference.child(friend_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    callBack.onError(KEY_NOT_FOUND, friend_key + " ile ilişkili kullanıcı bulunamadı");
                    return;
                }
                @SuppressWarnings("unchecked")
                ArrayList<String> friend_reqs = (ArrayList<String>)
                        snapshot.child("friend_reqs").getValue();
                if (friend_reqs == null) {
                    friend_reqs = new ArrayList<>();
                }
                friend_reqs.add(user_key);
                handler.addToRequests(friend_key, friend_reqs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    /**
     * It searches given key in users friend requests, if it shows up calls addASFriend,
     * if not calls SendFriendRequestInner method. It is a private method as it does not make controls
     * and should not be used directly, but should be called after control method
     * <p>
     * ERRORS: <p>
     * {@value DATABASE_ERROR} <p>
     * {@value KEY_NOT_FOUND} <p>
     *
     * @param user_key     key of this user, whose friend requests list will be searched
     * @param searched_key key of the other user, which is searched in this users requests list
     * @param callBack     the callBack to be called whenever an error occurs or task successfully end
     * @see #sendFriendRequestInner(String, String, DatabaseCallBack)
     * @see #addAsFriend(String, String, DatabaseCallBack)
     */
    private void searchInFriendRequestsList(@NonNull final String user_key,
                                            @NonNull final String searched_key,
                                            @NonNull final DatabaseCallBack callBack) {
        user_reference.child(user_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    callBack.onError(KEY_NOT_FOUND, user_key + " ile ilişkili kullanıcı bulunamadı");
                    return;
                }
                @SuppressWarnings("unchecked")
                ArrayList<String> friend_reqs = (ArrayList<String>)
                        snapshot.child("friend_reqs").getValue();
                if (friend_reqs != null) {
                    for (String key : friend_reqs) {
                        if (searched_key.equals(key)) {
                            addAsFriend(user_key, searched_key, callBack);
                            return;
                        }
                    }
                }
                sendFriendRequestInner(user_key, searched_key, callBack);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    /**
     * Creates new node in friends snapshot, put nodes key inside both users friend list
     * <p>
     * ERRORS: <p>
     * {@value DATABASE_ERROR} <p>
     * {@value KEY_NOT_FOUND} <p>
     * {@value USER_AND_TARGET_KEY_SAME} <p>
     * {@value ALREADY_FRIENDS} <p>
     *
     * @param user_key   key of this user
     * @param friend_key key of friend request
     * @param callBack   the callBack to be called whenever an error occurs or task successfully end
     */
    public void addAsFriend(final String user_key, final String friend_key, final DatabaseCallBack callBack) {
        if (user_key == null) {
            callBack.onError(KEY_NOT_FOUND, "kullanıcı keyi null");
            return;
        }
        if (friend_key == null) {
            callBack.onError(KEY_NOT_FOUND, "aranan key null");
            return;
        }
        boolean same_key = false;
        if (user_key.equals(friend_key)) {
            same_key = true;
        }
        final AddFriendHandler handler = new AddFriendHandler() {
            @Override
            public void addAsFriend(String key, ArrayList<String> friends) {
                user_reference.child(key).child("friends").setValue(friends);
            }

            @Override
            public void removeFromRequests(String key, ArrayList<String> friend_reqs) {
                user_reference.child(key).child("friend_reqs").setValue(friend_reqs);
            }

            @Override
            public void removeFriendsOnError(String friend_list_key) {
                friend_reference.child(friend_list_key).setValue(null);
            }
        };
        final boolean finalSame_key = same_key;
        final String friend_list_key = friend_reference.push().getKey();
        assert friend_list_key != null;
        ArrayList<String> friends = new ArrayList<>();
        friends.add(user_key);
        friends.add(friend_key);
        friend_reference.child(friend_list_key).child("friends").setValue(friends);
        user_reference.child(user_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    callBack.onError(KEY_NOT_FOUND, user_key + " ile ilişkili kullanıcı bulunamadı");
                    handler.removeFriendsOnError(friend_list_key);
                    return;
                }
                @SuppressWarnings("unchecked")
                ArrayList<String> friends = (ArrayList<String>)
                        snapshot.child("friends").getValue();
                if (friends == null) {
                    friends = new ArrayList<>();
                }
                friends.add(friend_list_key);
                @SuppressWarnings("unchecked")
                ArrayList<String> friend_reqs = (ArrayList<String>)
                        snapshot.child("friend_reqs").getValue();
                if (friend_reqs == null) {
                    friend_reqs = new ArrayList<>();
                }
                friend_reqs.remove(friend_key);
                if (finalSame_key) {
                    handler.removeFromRequests(user_key, friend_reqs);
                    handler.removeFriendsOnError(friend_list_key);
                    callBack.onError(USER_AND_TARGET_KEY_SAME, "Arkadaş eklenecek key ile kullanıcı keyi aynı");
                    return;
                }
                if (friends.contains(friend_key)) {
                    handler.removeFromRequests(user_key, friend_reqs);
                    handler.removeFriendsOnError(friend_list_key);
                    callBack.onError(ALREADY_FRIENDS, "zaten arkadaşlar");
                    return;
                }
                handler.addAsFriend(user_key, friends);
                handler.removeFromRequests(user_key, friend_reqs);
                callBack.onSuccess("arkadaş eklendi");
                notificationListener.onPersonalEvent("friend_add",
                        friend_key,
                        snapshot.child("image").getValue().toString(),
                        snapshot.child("name").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
        user_reference.child(friend_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    callBack.onError(KEY_NOT_FOUND, friend_key + " ile ilişkili kullanıcı bulunamadı");
                    handler.removeFriendsOnError(friend_list_key);
                    return;
                }
                @SuppressWarnings("unchecked")
                ArrayList<String> friends = (ArrayList<String>)
                        snapshot.child("friends").getValue();
                if (friends == null) {
                    friends = new ArrayList<>();
                }
                friends.add(friend_list_key);
                @SuppressWarnings("unchecked")
                ArrayList<String> friend_reqs = (ArrayList<String>)
                        snapshot.child("friend_reqs").getValue();
                if (friend_reqs == null) {
                    friend_reqs = new ArrayList<>();
                }
                friend_reqs.remove(user_key);
                if (finalSame_key) {
                    handler.removeFromRequests(friend_key, friend_reqs);
                    handler.removeFriendsOnError(friend_list_key);
                    //no error will be send as it would be send in user_key's snapshot
                    return;
                }
                if (friends.contains(user_key)) {
                    handler.removeFromRequests(friend_key, friend_reqs);
                    handler.removeFriendsOnError(friend_list_key);
                    //no error will be send as it would be send in user_key's snapshot
                    return;
                }
                handler.addAsFriend(friend_key, friends);
                handler.removeFromRequests(friend_key, friend_reqs);
                notificationListener.onPersonalEvent("friend_add",
                        user_key,
                        snapshot.child("image").getValue().toString(),
                        snapshot.child("name").getValue().toString());
                //no success will be send as it would be send in user_key's snapshot
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    /**
     * Removes friend request from this users friend requests list
     * <p>
     * ERRORS: <p>
     * {@value DATABASE_ERROR} <p>
     * {@value KEY_NOT_FOUND} <p>
     * {@value USER_AND_TARGET_KEY_SAME} <p>
     * {@value VALUE_NOT_FOUND} <p>
     *
     * @param user_key    key of this user
     * @param request_key key of friend request
     * @param callBack    the callBack to be called whenever an error occurs or task successfully end
     */
    public void declineFriendRequest(final String user_key,
                                     final String request_key,
                                     final DatabaseCallBack callBack) {
        if (user_key == null) {
            callBack.onError(KEY_NOT_FOUND, "kullanıcı keyi null");
            return;
        }
        if (request_key == null) {
            callBack.onError(KEY_NOT_FOUND, "aranan key null");
            return;
        }
        boolean same_key = false;
        if (user_key.equals(request_key)) {
            same_key = true;
        }
        final DeclineFriendRequestHandler handler = new DeclineFriendRequestHandler() {
            @Override
            public void declineFriendRequest(String key, ArrayList<String> friend_reqs) {
                user_reference.child(key).child("friend_reqs").setValue(friend_reqs);
            }
        };
        final boolean finalSame_key = same_key;
        user_reference.child(user_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    callBack.onError(KEY_NOT_FOUND, user_key + " ile ilişkili kullanıcı bulunamadı");
                    return;
                }
                @SuppressWarnings("unchecked")
                ArrayList<String> friend_reqs = (ArrayList<String>)
                        snapshot.child("friend_reqs").getValue();
                if (friend_reqs == null) {
                    friend_reqs = new ArrayList<>();
                }
                boolean is_request_exists = friend_reqs.remove(request_key);
                if (!is_request_exists) {
                    callBack.onError(VALUE_NOT_FOUND, "Böyle bir istek bulunamadı");
                    return;
                }
                handler.declineFriendRequest(user_key, friend_reqs);
                if (finalSame_key) {
                    callBack.onError(USER_AND_TARGET_KEY_SAME, "Silinecek isteğin keyi ile kullanıcı keyi aynı");
                    //even though decline method successfully called, still an error will be sent,
                    //as it should not be happening in the first place
                }
                callBack.onSuccess("İstek silindi");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    /**
     * Removes friend from friends snapshot and removes snapshots key from both users.
     * <p>
     * ERRORS: <p>
     * see removeFriendInner and searchInFriends for error list<p>
     *
     * @param user_key   key of this user
     * @param friend_key key of friend
     * @param callBack   the callBack to be called whenever an error occurs or task successfully end
     * @see #removeFriendInner(String, String, String, RemoveFriendHandler, DatabaseCallBack)
     * @see #searchInFriends(String, String, DatabaseCallBack)
     */
    public void removeFriend(final String user_key,
                             final String friend_key,
                             final DatabaseCallBack callBack) {
        if (user_key == null) {
            callBack.onError(KEY_NOT_FOUND, "kullanıcı keyi null");
            return;
        }
        if (friend_key == null) {
            callBack.onError(KEY_NOT_FOUND, "silinecek key null");
            return;
        }
        final RemoveFriendHandler handler = new RemoveFriendHandler() {
            @Override
            public void removeFriend(String key) {
                friend_reference.child(key).setValue(null);
            }

            @Override
            public void removeFromFriendList(String key, ArrayList<String> friends) {
                user_reference.child(key).child("friends").setValue(friends);
            }
        };
        final DatabaseCallBack innerCallBack = new DatabaseCallBack() {
            @Override
            public void onSuccess(String friend_list_key) {
                removeFriendInner(user_key, friend_key, friend_list_key, handler, callBack);
            }

            @Override
            public void onError(String error_tag, String error) {
                callBack.onError(error_tag, "böyle bir arkadaş bulunamadı");
            }
        };
        searchInFriends(user_key, friend_key, innerCallBack);
    }

    /**
     * Removes friend from friends snapshot and removes snapshots key from both users.
     * It is a private method, controls must be done before calling this method
     * <p>
     * ERRORS: <p>
     * {@value DATABASE_ERROR} <p>
     * {@value KEY_NOT_FOUND} <p>
     * {@value VALUE_NOT_FOUND} <p>
     *
     * @param user_key        key of this user
     * @param friend_key      key of friend
     * @param friend_list_key key of friends snapshot
     * @param handler         handler for removing friend and keys from friend list
     * @param callBack        the callBack to be called whenever an error occurs or task successfully end
     */
    private void removeFriendInner(@NonNull final String user_key,
                                   @NonNull final String friend_key,
                                   @NonNull final String friend_list_key,
                                   @NonNull final RemoveFriendHandler handler,
                                   @NonNull final DatabaseCallBack callBack) {
        user_reference.child(user_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    callBack.onError(KEY_NOT_FOUND, user_key + " ile ilişkili kullanıcı bulunamadı");
                    return;
                }
                @SuppressWarnings("unchecked")
                ArrayList<String> friends = (ArrayList<String>)
                        snapshot.child("friends").getValue();
                if (friends == null) {
                    friends = new ArrayList<>();
                }
                boolean is_friend_exists = friends.remove(friend_list_key);
                if (!is_friend_exists) {
                    callBack.onError(VALUE_NOT_FOUND, "Böyle bir arkadaş bulunamadı");
                    return;
                }
                handler.removeFromFriendList(user_key, friends);
                handler.removeFriend(friend_list_key);
                callBack.onSuccess("Arkadaş silindi");
                final String user_name = snapshot.child("name").getValue().toString();
                final String user_foto = snapshot.child("image").getValue().toString();
                getFriend(friend_key, friend_list_key, new FriendCallBack() {
                    @Override
                    public void onFriendRetrieveSuccess(Friend friend) {
                        notificationListener.onPersonalEvent("friend_remove",
                                friend_key,
                                user_foto,
                                user_name);
                        notificationListener.onPersonalEvent("friend_remove",
                                user_key,
                                friend.getPerson_image().toString(),
                                friend.getName());
                    }

                    @Override
                    public void onError(String error_tag, String error) {
                        Log.e(error_tag, error);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
        user_reference.child(friend_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    callBack.onError(KEY_NOT_FOUND, friend_key + " ile ilişkili kullanıcı bulunamadı");
                    return;
                }
                @SuppressWarnings("unchecked")
                ArrayList<String> friends = (ArrayList<String>)
                        snapshot.child("friends").getValue();
                if (friends == null) {
                    friends = new ArrayList<>();
                }
                boolean is_friend_exists = friends.remove(friend_list_key);
                if (!is_friend_exists) {
                    callBack.onError(VALUE_NOT_FOUND, "Böyle bir arkadaş bulunamadı");
                    return;
                }
                handler.removeFromFriendList(friend_key, friends);
                //no success will be send as it would be send in user_key's snapshot
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    /**
     *  saves last month's saving. (budget - total expense)
     * @param saving    last month's saving
     */
    public void setLastMonthSaving(int saving){
        user_reference.child(person.getKey()).child("last_month_saving").setValue(saving);
    }

    /**
     * Returns last month's saving with callback
     */
    public void getLastMonthSaving(final BudgetCallBack callBack){
        user_reference.child(person.getKey()).child("last_month_saving").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    callBack.onError(VALUE_NOT_FOUND, "önceki ay tasarrufu bulunamadı");
                    return;
                }
                callBack.onBudgetRetrieveSuccess((Integer) snapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    public void getBudget(final BudgetCallBack callBack) {
        user_reference.child(person.getKey()).child("budget").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    callBack.onError(VALUE_NOT_FOUND, "bütçe bulunamadı");
                    return;
                }
                int budget = Integer.parseInt(snapshot.getValue().toString());
                callBack.onBudgetRetrieveSuccess(budget);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    public void setBudget(final int budget) {
        user_reference.child(person.getKey()).child("budget").setValue(budget);
        user_reference.child(person.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificationListener.onPersonalEvent("budget_change", person.getKey(),
                        snapshot.child("image").getValue().toString(),
                        String.valueOf(budget));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addExpense(String name, String type, String price, DatabaseCallBack callBack) {
        long date = System.currentTimeMillis();
        DatabaseReference dbRef = user_reference.child(person.getKey()).child("expenses");
        String key = dbRef.push().getKey();
        DatabaseReference dbr = dbRef.child(key);
        dbr.child("name").setValue(name);
        dbr.child("type").setValue(type);
        dbr.child("price").setValue(price);
        dbr.child("date").setValue(date);
        callBack.onSuccess("harcama eklendi");
    }

    public void addExpenseToFriends(String name, String type, final String price, String friendshipKey, String date) {
        DatabaseReference dbRef = friend_reference.child(friendshipKey).child("expenses");
        String key = dbRef.push().getKey();
        DatabaseReference dbr = dbRef.child(key);
        dbr.child("name").setValue(name);
        dbr.child("type").setValue(type);
        dbr.child("price").setValue(price);
        dbr.child("addedBy").setValue(person.getName());
        dbr.child("date").setValue(date);

        //debt info
        final DatabaseReference dbDebts = friend_reference.child(friendshipKey).child("debts").child(person.getKey());
        dbDebts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    dbDebts.setValue(String.valueOf(Integer.parseInt(price) / 2));
                } else {
                    dbDebts.setValue(String.valueOf(Float.parseFloat(snapshot.getValue().toString()) + Float.parseFloat(price) / 2));    //kullanıcının arkadaşından alması gereken para miktarı
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addExpenseToGroups(String name, String type, final String price, String groupsKey, String date, final ArrayList<String> groupMembers) {
        DatabaseReference dbRef = group_reference.child(groupsKey).child("expenses");
        String key = dbRef.push().getKey();
        DatabaseReference dbr = dbRef.child(key);
        dbr.child("name").setValue(name);
        dbr.child("type").setValue(type);
        dbr.child("price").setValue(price);
        dbr.child("addedBy").setValue(person.getName());
        dbr.child("addedById").setValue(person.getKey());
        dbr.child("date").setValue(date);

        //debt info
        final DatabaseReference dbDebts = group_reference.child(groupsKey).child("debts").child(person.getKey());
        for (final String member : groupMembers) {
            if (!member.equals(person.getKey())) {
                dbDebts.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            dbDebts.child(member).setValue(String.valueOf(Integer.parseInt(price) / groupMembers.size()));
                        } else {
                            boolean ctrlExist = false;
                            for(DataSnapshot ds : snapshot.getChildren()){
                                if(ds.getKey().equals(member)) {
                                    ctrlExist=true;
                                }
                            }
                            if(!ctrlExist){
                                dbDebts.child(member).setValue(String.valueOf(Integer.parseInt(price) / groupMembers.size()));
                            }else {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    if (ds.getKey().equals(member)) {
                                        dbDebts.child(member).setValue(String.valueOf(Float.parseFloat(ds.getValue().toString()) + Float.parseFloat(price) / groupMembers.size()));    //kullanıcının arkadaşından alması gereken para miktarı
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }

    public void getExpensesFromFriend(String friendshipKey, final TransferCallBack callBack){
        final ArrayList<Transfers> expenses = new ArrayList<>();
        DatabaseReference dbRef = friend_reference.child(friendshipKey).child("expenses");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                expenses.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = (String) ds.child("name").getValue();
                    String addedBy = (String) ds.child("addedBy").getValue();
                    String type = (String) ds.child("type").getValue();
                    String price = (String) ds.child("price").getValue();
                    String date = (String) ds.child("date").getValue();
                    int image = R.drawable.ic_other;
                    ;
                    if (type != null && name != null && price != null && addedBy != null) {
                        if (type.equals("Yiyecek")) {
                            image = R.drawable.ic_baseline_fastfood_24;
                        } else if (type.equals("Giyecek")) {
                            image = R.drawable.ic_baseline_wear_24;
                        } else if (type.equals("Temizlik")) {
                            image = R.drawable.ic_baseline_hygiene_24;
                        } else if (type.equals("Kırtasiye")) {
                            image = R.drawable.ic_baseline_school_24;
                        } else if (type.equals("Diğer")) {
                            image = R.drawable.ic_other;
                        }
                        expenses.add(new Transfers(0, image, name, addedBy, price, date));
                    }
                }
                callBack.onTransferRetrieveSuccess(expenses);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    public void getExpensesFromGroup(String groupKey, final TransferCallBack callBack){
        final ArrayList<Transfers> expenses = new ArrayList<>();
        DatabaseReference dbRef = group_reference.child(groupKey).child("expenses");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                expenses.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = (String) ds.child("name").getValue();
                    String addedBy = (String) ds.child("addedBy").getValue();
                    String type = (String) ds.child("type").getValue();
                    String price = (String) ds.child("price").getValue();
                    String date = (String) ds.child("date").getValue();
                    int image = R.drawable.ic_other;
                    if (type != null && name != null && price != null && addedBy != null) {
                        if (type.equals("Yiyecek")) {
                            image = R.drawable.ic_baseline_fastfood_24;
                        } else if (type.equals("Giyecek")) {
                            image = R.drawable.ic_baseline_wear_24;
                        } else if (type.equals("Temizlik")) {
                            image = R.drawable.ic_baseline_hygiene_24;
                        } else if (type.equals("Kırtasiye")) {
                            image = R.drawable.ic_baseline_school_24;
                        } else if (type.equals("Diğer")) {
                            image = R.drawable.ic_other;
                        }
                        expenses.add(new Transfers(0, image, name, addedBy, price, date));
                    }
                }
                callBack.onTransferRetrieveSuccess(expenses);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    public void getExpenses(final ExpenseCallBack callBack) {
        final ArrayList<Expense> expenses = new ArrayList<>();
        DatabaseReference dbRef = user_reference.child(person.getKey()).child("expenses");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    callBack.onError(VALUE_NOT_FOUND, "harcamalar bulunamadı");
                    return;
                }
                int totExpense = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = (String) ds.child("name").getValue();
                    String type = (String) ds.child("type").getValue();
                    String price = (String) ds.child("price").getValue();
                    if (price != null) {
                        int p = Integer.parseInt(price);
                        totExpense += p;
                        expenses.add(new Expense(name, type, price));
                    }
                }
                callBack.onExpenseRetrieveSuccess(expenses);
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    /**
    *   This method is called to create a new group. Used in AddgGroupsFragment
    *   Group information is kept in the database both within the group and the user.
    */
    public void createNewGroup(final String person_id, ArrayList<Friend> checked_personList, final String group_type,
                               @NotNull final String groupName, final DatabaseCallBack callBack) {
        if (!groupName.isEmpty()) {
            if (checked_personList.size() > 0) {
                final String group_name = groupName.trim();
                Groups group = new Groups(group_name, group_type);
                group.addFriend(person_id);
                for (Friend friend : checked_personList) {
                    String friendKey = friend.getKey();
                    group.addFriend(friendKey);
                }
                final String group_key = group_reference.push().getKey();
                assert group_key != null;
                group.setKey(group_key);
                group_reference.child(group_key).setValue(group);

                final DatabaseReference cur_user_ref = user_reference.child(person_id);
                cur_user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        @SuppressWarnings("unchecked")
                        ArrayList<String> current_users_groups = (ArrayList<String>) snapshot.child("groups").getValue();
                        if (current_users_groups == null) {
                            current_users_groups = new ArrayList<>();
                        }
                        if (current_users_groups.contains(group_key)) {
                            callBack.onError(ALREADY_FRIENDS, "Zaten bu grupta varsın");
                        } else {
                            current_users_groups.add(group_key);
                        }
                        cur_user_ref.child("groups").setValue(current_users_groups);
                        notificationListener.onPersonalEvent("group_add", person_id, group_type,
                                groupName);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callBack.onError(DATABASE_ERROR, error.getMessage());
                    }
                });


                for (Friend friend : AddGroupsAdapter.checked_personList) {
                    final String friendKey = friend.getKey();
                    final DatabaseReference user_ref = user_reference.child(friendKey);
                    user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                callBack.onError(KEY_NOT_FOUND, "kullanıcı bulunamadı");
                            }
                            @SuppressWarnings("unchecked")
                            ArrayList<String> users_groups = (ArrayList<String>) snapshot.child("groups").getValue();
                            if (users_groups == null) {
                                users_groups = new ArrayList<>();
                            }

                            if (users_groups.contains(group_key)) {
                                callBack.onError(ALREADY_IN_GROUP, "Zaten bu grupta varsın");
                            } else {
                                users_groups.add(group_key);
                            }
                            user_ref.child("groups").setValue(users_groups);
                            notificationListener.onPersonalEvent("group_add", friendKey, group_type,
                                    groupName);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            callBack.onError(DATABASE_ERROR, error.getMessage());
                        }
                    });
                }
                callBack.onSuccess("Grup başarılıyla oluşturuldu");
            } else {
                callBack.onError(NO_SELECTED_FRIEND, "Lütfen arkadaş seçiniz");
            }
        } else {
            callBack.onError(NO_GIVEN_GROUP_NAME, "Lütfen grup ismi giriniz");
        }
    }

    /**
    *   Pulls the data of all groups from the database. Updates GroupsFragment
    */
    public void getAllGroups(final String person_id, final ArrayList<Groups> groupsArrayList, final GroupAdapter groupAdapter) {
        group_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupsArrayList.clear();
                for (final DataSnapshot d : snapshot.getChildren()) {
                    DataSnapshot gmembers_snapshot = d.child("group_members");
                    for (DataSnapshot d2 : gmembers_snapshot.getChildren()) {
                        if (d2.getValue().equals(person_id)) {
                            final Groups group = d.getValue(Groups.class);
                            group.setGroupKey(d.getKey());
                            groupsArrayList.add(group);
                            Database.GetMemberInfoCallBack infoCallBack = new GetMemberInfoCallBack() {
                                @Override
                                public void onGetMemberInfoRetrieveSuccess(ArrayList<Friend> members) {
                                    Database.getDebtFromGroupCallBack debtCallBack = new getDebtFromGroupCallBack() {
                                        @Override
                                        public void onGetDebtFromGroupRetrieveSuccess(float debt) {
                                            group.addDebt(debt);
                                            groupAdapter.setTotDebt(0);
                                            groupAdapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onError(String error_tag, String error) {

                                        }
                                    };
                                    for(Friend friend : members){
                                        if(!getPerson().getKey().equals(friend.getKey())) {
                                            friend.setFriendshipsKey(group.getKey());
                                            getDebtFromGroups(getPerson().getKey(), friend, debtCallBack);
                                        }
                                    }
                                }

                                @Override
                                public void onError(String error_tag, String error) {

                                }
                            };
                            ArrayList<Friend> members = new ArrayList<>();
                            getGroupMembersInfo(group.getGroup_members(),members,infoCallBack);
                        }
                    }
                }
                groupAdapter.setTotDebt(0);
                groupAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

     /**
     *  It calls the group members whose keys are kept in the database as a friend object.
     */
    public void getGroupMembersInfo(final ArrayList<String> groupMembers, final ArrayList<Friend> members, final GetMemberInfoCallBack callBack) {

        DatabaseReference dbRef = user_reference;
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    for (String member : groupMembers) {
                        if (ds.getKey().toString().equals(member)) {
                            String name = (String) ds.child("name").getValue();
                            String image = (String) ds.child("image").getValue();
                            members.add(new Friend(image, name, member));
                        }
                    }
                }

                callBack.onGetMemberInfoRetrieveSuccess(members);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getDebtFromFriend(final String userId, Friend friend, final getDebtFromFriendCallBack callBack) {
        DatabaseReference dbRef = friend_reference.child(friend.getFriendshipsKey()).child("debts");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float credit = 0;         //the amount of money the user must receive from the other user
                float debt = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.getKey().equals(userId)) {
                        credit += Float.parseFloat((String) ds.getValue());
                    } else {
                        debt += Float.parseFloat((String) ds.getValue());
                    }
                }
                debt = debt - credit;
                if (debt <= 0) {
                    debt = 0;
                }
                callBack.onGetDebtFromFriendRetrieveSuccess(debt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError("Error : ", "Borç verisi hatalı.");
            }
        });

    }

    public void payToFriend(final String userId, final Friend friend, final String amount) {
        final boolean[] ctrlFirst = {true};
        friend_reference.child(friend.getFriendshipsKey()).child("debts").child(friend.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (ctrlFirst[0]) {
                    final String newDebt = String.valueOf(Float.parseFloat(snapshot.getValue().toString()) - Float.parseFloat(amount));
                    friend_reference.child(friend.getFriendshipsKey()).child("debts").child(friend.getKey())
                            .setValue(newDebt);
                    friend_reference.child(friend.getFriendshipsKey()).child("debts").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!(snapshot.getValue() == null)) {
                                Float m = Float.parseFloat(snapshot.getValue().toString());//what user will take
                                if (m == Float.parseFloat(newDebt)) {
                                    friend_reference.child(friend.getFriendshipsKey()).child("debts").child(friend.getKey())
                                            .setValue("0");
                                    friend_reference.child(friend.getFriendshipsKey()).child("debts").child(userId)
                                            .setValue("0");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    ctrlFirst[0] = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getDebtFromGroups(final String userId, final Friend friend, final getDebtFromGroupCallBack callBack) {
        DatabaseReference dbRef = group_reference.child(friend.getFriendshipsKey()).child("debts").child(friend.getKey());
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    callBack.onGetDebtFromGroupRetrieveSuccess(0);
                } else {
                    for (final DataSnapshot ds : snapshot.getChildren()) {
                        if (ds.getKey().equals(userId)) {
                            DatabaseReference myRef = group_reference.child(friend.getFriendshipsKey()).child("debts").child(userId);
                            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot s) {
                                    if (s.getValue() == null) {
                                        callBack.onGetDebtFromGroupRetrieveSuccess(Float.parseFloat(ds.getValue().toString()));
                                    } else {
                                        for (DataSnapshot dds : s.getChildren()) {
                                            if (dds.getKey().equals(friend.getKey())) {
                                                //the amount of money the user must receive from the other user
                                                float credit = 0;
                                                float debt = 0;
                                                debt += Float.parseFloat(ds.getValue().toString());
                                                credit += Float.parseFloat(dds.getValue().toString());
                                                debt = debt - credit;
                                                if (debt <= 0) {
                                                    debt = 0;
                                                }
                                                callBack.onGetDebtFromGroupRetrieveSuccess(debt);
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    callBack.onError(DATABASE_ERROR, error.getMessage());
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    public void payToGroupsMember(final String userId, final Friend friend, final String amount) {
        final boolean[] ctrlFirst = {true};
        group_reference.child(friend.getFriendshipsKey()).child("debts").child(friend.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (ctrlFirst[0]) {
                    System.out.println("dogruuuu2");
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (ds.getKey().equals(userId)) {
                            System.out.println("dogruuu3");
                            System.out.println("hesapppp : " + ds.getValue().toString());
                            final String newDebt = String.valueOf(Float.parseFloat(ds.getValue().toString()) - Float.parseFloat(amount));
                            System.out.println("hesap 2 2 : " + newDebt);
                            group_reference.child(friend.getFriendshipsKey()).child("debts").child(friend.getKey()).child(userId)
                                    .setValue(newDebt);
                            group_reference.child(friend.getFriendshipsKey()).child("debts").child(userId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot d : snapshot.getChildren()) {
                                                if (d.getKey().equals(friend.getKey())) {
                                                    System.out.println("eşitlendi");
                                                    Float m = Float.parseFloat(d.getValue().toString());           //benim ondan alacağım
                                                    if (m == Float.parseFloat(newDebt)) {
                                                        group_reference.child(friend.getFriendshipsKey()).child("debts").child(friend.getKey()).child(userId)
                                                                .setValue("0");
                                                        group_reference.child(friend.getFriendshipsKey()).child("debts").child(userId).child(friend.getKey())
                                                                .setValue("0");
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                    }
                    ctrlFirst[0] = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getSelectedGroup(final String groupId, final GroupCallBack callBack) {
        group_reference.child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    callBack.onError(KEY_NOT_FOUND, "group keyi bulunamadı");
                    return;
                }
                Groups group = snapshot.getValue(Groups.class);
                callBack.onGroupRetrieveSuccess(group);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    public void updateGroup(String group_key, String group_type, @NotNull String groupName, final DatabaseCallBack callBack) {
        if (!groupName.isEmpty()) {
            final String group_name = groupName.trim();
            group_reference.child(group_key).child("group_name").setValue(group_name);
            group_reference.child(group_key).child("group_type").setValue(group_type);
            callBack.onSuccess("Değişikliler başarıyla kaydedildi");

        } else {
            callBack.onError(NO_GIVEN_GROUP_NAME, "Lütfen grup ismi giriniz");
        }
    }

    public void addUserToGroup(Groups group, ArrayList<Friend> checked_personList, final DatabaseCallBack callBack) {
        final String groupKey = group.getKey();
        if (checked_personList.size() > 0) {
            for (Friend friend : checked_personList) {
                final String friendKey = friend.getKey();
                Log.d(TAG, friendKey);
                if (group.getGroup_members().contains(friendKey)) {
                    callBack.onError(ALREADY_IN_GROUP, "Zaten bu gruba kayıtlı");
                    return;
                }
                group.addFriend(friendKey);

                final DatabaseReference user_groups_ref = user_reference.child(friendKey).child("groups");
                user_groups_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            ArrayList<String> current_users_groups = new ArrayList<String>();
                            current_users_groups.add(groupKey);
                            user_groups_ref.setValue(current_users_groups);
                        } else {
                            ArrayList<String> current_users_groups = (ArrayList<String>) snapshot.getValue();
                            if (!current_users_groups.contains(groupKey)) {
                                current_users_groups.add(groupKey);
                            } else {
                                callBack.onError(ALREADY_IN_GROUP, "Zaten bu gruba kayıtlı");
                                return;
                            }
                            user_groups_ref.setValue(current_users_groups);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callBack.onError(DATABASE_ERROR, error.getMessage());
                    }
                });
            }
            final ArrayList<String> friend_keys = new ArrayList<>();
            for (Friend friend : checked_personList) {
                friend_keys.add(friend.getKey());
            }
            final DatabaseReference group_ref = group_reference.child(groupKey).child("group_members");
            group_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<String> current_group_members = (ArrayList<String>) snapshot.getValue();
                    current_group_members.addAll(friend_keys);
                    group_ref.setValue(current_group_members);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    callBack.onError(DATABASE_ERROR, error.getMessage());
                }
            });

            callBack.onSuccess("Kişi/-ler, gruba başarıyla eklendi");
        } else {
            callBack.onError(NO_SELECTED_FRIEND, "Lütfen arkadaş seçiniz");
        }
    }

    /**
    *  It is used to delete friends from the group.
    *  Checks the number of group members.
    *  If there is only one person left in the group, the group is deleted
    */
    public void removeFromGroup(final String selected_person_id, final Groups group, final DatabaseCallBack callBack) {
        if (selected_person_id == null) {
            callBack.onError(KEY_NOT_FOUND, "silinecek key null");
            return;
        }
        final String group_key = group.getKey();

        final DatabaseReference user_groups_ref = user_reference.child(selected_person_id).child("groups");
        user_groups_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    callBack.onError(VALUE_NOT_FOUND, "Burası boş");
                    return;
                }
                @SuppressWarnings("unchecked")
                ArrayList<String> current_users_groups = (ArrayList<String>) snapshot.getValue();
                if (current_users_groups.contains(group_key)) {
                    current_users_groups.remove(group_key);
                } else {
                    callBack.onError(ALREADY_REMOVED_FROM_GROUP, "Bu gruptan zaten silinmiş");
                    return;
                }
                user_groups_ref.setValue(current_users_groups);
                notificationListener.onPersonalEvent("group_remove", selected_person_id, group.getGroup_type(), group.getGroup_name());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });

        int group_members_size = group.getGroup_members().size();
        if (group_members_size <= 2) {
            group_reference.child(group_key).setValue(null);
            group.removeFriend(selected_person_id);
            final String last_person_key = group.getGroup_members().get(0);
            final DatabaseReference l_user_groups_ref = user_reference.child(last_person_key).child("groups");
            l_user_groups_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        callBack.onError(VALUE_NOT_FOUND, "Burası boş");
                        return;
                    }
                    @SuppressWarnings("unchecked")
                    ArrayList<String> current_users_groups = (ArrayList<String>) snapshot.getValue();
                    if (current_users_groups.contains(group_key)) {
                        current_users_groups.remove(group_key);
                    } else {
                        callBack.onError(ALREADY_REMOVED_FROM_GROUP, "Bu gruptan zaten silinmiş");
                        return;
                    }
                    l_user_groups_ref.setValue(current_users_groups);
                    callBack.onSuccess("Grup silindi");
                    notificationListener.onPersonalEvent("group_remove", last_person_key, group.getGroup_type(), group.getGroup_name());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    callBack.onError(DATABASE_ERROR, error.getMessage());
                }
            });
        } else {
            group.removeFriend(selected_person_id);
            final String groupKey = group.getKey();
            final DatabaseReference group_ref = group_reference.child(groupKey).child("group_members");
            group_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<String> current_group_members = (ArrayList<String>) snapshot.getValue();
                    if (current_group_members.contains(selected_person_id)) {
                        current_group_members.remove(selected_person_id);
                    } else {
                        callBack.onError(ALREADY_IN_GROUP, "Zaten bu gruba kayıtlı");
                        return;
                    }
                    group_ref.setValue(current_group_members);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    callBack.onError(DATABASE_ERROR, error.getMessage());
                }
            });
        }
        callBack.onSuccess("Gruptan başarıyla çıkarıldı");
    }

    public void deleteGroup(final Groups group, final DatabaseCallBack callBack) {
        final String group_key = group.getKey();
        final ArrayList<String> group_members_id = group.getGroup_members();
        for (String friend_id : group_members_id) {
            final String friend_id_final = friend_id;
            final DatabaseReference user_groups_ref = user_reference.child(friend_id).child("groups");
            user_groups_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    @SuppressWarnings("unchecked")
                    ArrayList<String> current_users_groups = (ArrayList<String>) snapshot.getValue();
                    if (current_users_groups.contains(group_key)) {
                        current_users_groups.remove(group_key);
                    } else {
                        callBack.onError(ALREADY_REMOVED_FROM_GROUP, "Bu gruptan zaten silinmiş");
                    }
                    user_groups_ref.setValue(current_users_groups);
                    notificationListener.onPersonalEvent("group_remove", friend_id_final, group.getGroup_type(), group.getGroup_name());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        group_reference.child(group_key).setValue(null);
        callBack.onSuccess("Grup başarıyla silindi");
    }
}