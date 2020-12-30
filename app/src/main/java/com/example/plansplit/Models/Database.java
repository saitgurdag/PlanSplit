package com.example.plansplit.Models;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.plansplit.Controllers.Adapters.AddGroupsAdapter;
import com.example.plansplit.Controllers.Adapters.GroupOperationsAdapter;
import com.example.plansplit.Controllers.FragmentControllers.personal.PersonalFragment;
import com.example.plansplit.Models.Objects.Expense;
import com.example.plansplit.Models.Objects.Friend;
import com.example.plansplit.Models.Objects.FriendRequest;
import com.example.plansplit.Models.Objects.Groups;
import com.example.plansplit.Models.Objects.Person;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

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
    private static final String ALREADY_DELETED_GROUP = "ALREADY_DELETED_GROUP";


    //Firebase
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference user_reference = database.getReference("users");
    private static final DatabaseReference friend_reference = database.getReference("friends");
    private static final DatabaseReference group_reference = database.getReference("groups");

    private static final String TAG = "DATABASE";
    private static final String addGroupsFragment = "AddGroupsFragment";
    final int[] butce = new int[1];
    public boolean ctrlRun = false;
    private Context context;
    private Fragment fragment;
    private int totExpense = 0;

    public Database(Object... o){
        if(o.length==1){
            this.context =(Context) o[0];
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(context);
            if (acct != null) {
                setUserId(acct.getId());
                System.out.println("acct not null");
            } else {
                System.out.println("acct null");
            }
        }else if(o.length==2){
            this.context = (Context) o[0];
            this.fragment = (Fragment) o[1];
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(context);
            if (acct != null) {
                setUserId(acct.getId());
                System.out.println("acct not null");
            } else {
                System.out.println("acct null");
            }
        }
    }

    public int getTotExpense() {
        return totExpense;
    }

    public int getButce() {
        return butce[0];
    }

    private String userId = null;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }



    private static class Holder {
        private static final Database INSTANCE = new Database();
    }


    //Singleton
    public static Database getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Interface for receiving a Friend object.
     * One method with successfully retrieving the object, other one for errors
     */
    public interface FriendCallBack {
        void onFriendRetrieveSuccess(Friend friend);

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
     * Creates a child with "key" under user reference. No controls are made, so before use it
     * parameters should be checked
     *
     * @param key     Google ID
     * @param name    display name of google account
     * @param email   current email of google account
     * @param surname surname of google account
     */
    public void registerUser(String key, String name, String email, String surname,String image) {
        user_reference.child(key).child("name").setValue(name);
        user_reference.child(key).child("email").setValue(email);
        user_reference.child(key).child("surname").setValue(surname);
        user_reference.child(key).child("image").setValue(image);
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
     * @see #getFriend(String, FriendCallBack)
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
                                if (!user_key.equals(key)) {
                                    getFriend(key, callBack);
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
     * @param callBack   the callBack to be called whenever an error occurs or task successfully end
     * @see Friend
     */
    public void getFriend(final String friend_key, final FriendCallBack callBack) {
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
                /*fixme: kayıt sırasında resim almadığımızdan
                            rastgele resim koydum.
                            borçların database'de tutulma yöntemi geçici,
                            acilen değişmeli
                */
                if(!snapshot.child("image").exists()){
                     photo ="photo yok";
                }
                else{
                    photo = snapshot.child("image").getValue().toString();
                }

                String name = snapshot.child("name").getValue().toString();
                if (name.equals("No name")) {
                    //if there was one name when user login "No name" would be used, in such case
                    //using email as name would be better to show user to recognize the person
                    name = snapshot.child("email").getValue().toString();
                }
                int amount = 0;
                Friend friend = new Friend(photo, name, amount, friend_key);
                callBack.onFriendRetrieveSuccess(friend);
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
                /*fixme: kayıt sırasında resim almadığımızdan
                            rastgele resim koydum.
                */
                //int photo = R.drawable.denemeresim;
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
                    //fixme: although it is not an error message in order to use  this method
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
                                callBack.onError(VALUE_NOT_FOUND, "friend node'unun arkadaşları boş");
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

    public void getBudget() {
        user_reference.child(userId).child("budget").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String b;
                    b = snapshot.getValue().toString();
                    ((PersonalFragment) fragment).checkBudget(b);
                } else {
                    ((PersonalFragment) fragment).checkBudget(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setBudget(int budget) {
        user_reference.child(userId).child("budget").setValue(budget);
    }

    public void addExpense(String name, String type, String price) {

        DatabaseReference dbRef = user_reference.child(userId).child("expenses");
        String key = dbRef.push().getKey();
        DatabaseReference dbr = dbRef.child(key);
        dbr.child("name").setValue(name);
        dbr.child("type").setValue(type);
        dbr.child("price").setValue(price);
        getExpenses();

    }

    public ArrayList getExpenses() {
        final ArrayList<Expense> expenses = new ArrayList<>();
        DatabaseReference dbRef = user_reference.child(userId).child("expenses");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                totExpense = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = (String) ds.child("name").getValue();
                    String type = (String) ds.child("type").getValue();
                    String price = (String) ds.child("price").getValue();
                    if (price != null) {
                        int p = Integer.parseInt(price);
                        totExpense += p;
                        expenses.add(new Expense(name, type, p));
                    }

                }
                ((PersonalFragment) fragment).newExpense(expenses, totExpense);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        return expenses;
    }

    public void createNewGroup(String person_id, ArrayList<Friend> checked_personList, String group_type,
                               @NotNull EditText groupName_EditText, final DatabaseCallBack callBack) {
        if (!groupName_EditText.getText().toString().isEmpty()) {
            if (checked_personList.size() > 0) {
                final String group_key = group_reference.push().getKey();
                assert group_key != null;
                final String group_name = groupName_EditText.getText().toString().trim();
                Groups group = new Groups(group_name, group_type, group_key);
                group.addFriend(person_id);
                for (Friend friend : checked_personList) {
                    String friendKey = friend.getKey();
                    group.addFriend(friendKey);
                }

//                group.setKey(group_key);
                group_reference.child(group_key).setValue(group);
                //System.out.println(group.getKey()); check group_key;

                final DatabaseReference cur_user_ref = user_reference.child(person_id);
                cur_user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
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

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callBack.onError(DATABASE_ERROR, error.getMessage());
                    }
                });


                for (Friend friend : AddGroupsAdapter.checked_personList) {
                    String friendKey = friend.getKey();
                    final DatabaseReference user_ref = user_reference.child(friendKey);
                    user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                callBack.onError(KEY_NOT_FOUND, "kullanıcı bulunamadı");
                            }

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

    public void getAllGroups(final String person_id, final ArrayList<Groups> groupsArrayList, final DatabaseCallBack callBack) {
        group_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupsArrayList.clear();

                for (final DataSnapshot d : snapshot.getChildren()) {
                    /*System.out.println(person_id);
                    DataSnapshot personKeysnp = d.child("group_members").getChildren().iterator().next();
                    String personKey = (String) personKeysnp.getValue();
                    System.out.println(personKey);*/ //gruplarda bulunan kişiler

                    DataSnapshot gmembers_snapshot = d.child("group_members");
                    for (DataSnapshot d2 : gmembers_snapshot.getChildren()) {
                        if (d2.getValue().equals(person_id)) {
                            Groups group = d.getValue(Groups.class);
                            groupsArrayList.add(group);
                        }
                    }
                }
                callBack.onSuccess("Gruplar getirildi");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void getAllGroupMembers(Groups group, final ArrayList<Person> groupOperationsPersonList, final int background, final GroupOperationsAdapter adapter){
        final ArrayList<String> group_members_id = group.getGroup_members();

        user_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot d_members: snapshot.getChildren()){
                    if(group_members_id.contains(d_members.getKey())){
                        String user_image = (String) d_members.child("image").getValue();
                        Person person = new Person(background,user_image,"10");
                        groupOperationsPersonList.add(person);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void searchInGroups(String person_id, final String group_key, final DatabaseCallBack callBack){
        if (person_id == null) {
            callBack.onError(KEY_NOT_FOUND, "kullanıcı keyi null");
            return;
        }
        if (group_key == null) {
            callBack.onError(KEY_NOT_FOUND, "grup keyi key null");
            return;
        }

        user_reference.child(person_id).child("groups").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    callBack.onError(VALUE_NOT_FOUND, group_key);
                    return;
                }

                for(DataSnapshot d_group_key: snapshot.getChildren()){
                    if(d_group_key.getValue().equals(group_key)){
                        String cur_group_key = d_group_key.getKey();
                        System.out.println(cur_group_key);
                        assert cur_group_key != null;
                        callBack.onSuccess(cur_group_key);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });

    }

    public void getGroupMembersObjects(final ArrayList<Friend> group_members_objects, final ArrayList<String> group_members_ID){
        user_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot d_members: snapshot.getChildren()){
                    if(group_members_ID.contains(d_members.getKey())){
                        if(group_members_ID.get(0).equals(d_members.getKey())){
                            String user_image = (String) d_members.child("image").getValue();
                            String user_name = (String) d_members.child("name").getValue();
                            Friend cur_user = new Friend(user_image, user_name, group_members_ID.get(0));
                            System.out.println(cur_user.getName());
                            group_members_objects.add(cur_user);
                        }
                        Friend member = d_members.getValue(Friend.class);
                        group_members_objects.add(member);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateGroup(String group_key, String group_type, @NotNull EditText groupName_EditText, final DatabaseCallBack callBack){
        if (!groupName_EditText.getText().toString().isEmpty()){
            final String group_name = groupName_EditText.getText().toString().trim();
            group_reference.child(group_key).child("group_name").setValue(group_name);
            group_reference.child(group_key).child("group_type").setValue(group_type);
            callBack.onSuccess("Değişikliler başarıyla kaydedildi");

        }else {
            callBack.onError(NO_GIVEN_GROUP_NAME, "Lütfen grup ismi giriniz");
        }
    }

    public void addUserToGroup(Groups group, ArrayList<Friend> checked_personList, final DatabaseCallBack callBack){
        if(checked_personList.size() > 0){
            for (Friend friend : checked_personList){
                String friendKey = friend.getKey();
                final String groupKey = group.getKey();
                if(!group.getGroup_members().contains(friendKey)){
                    group.addFriend(friendKey);
                }

                group_reference.child(groupKey).setValue(group);

                final DatabaseReference user_groups_ref = user_reference.child(friendKey).child("groups");
                user_groups_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.exists()){
                            ArrayList<String> current_users_groups = new ArrayList<String>();
                            current_users_groups.add(groupKey);
                            user_groups_ref.setValue(current_users_groups);
                        }else{
                            ArrayList<String> current_users_groups = (ArrayList<String>) snapshot.getValue();
                            if (!current_users_groups.contains(groupKey)){
                                current_users_groups.add(groupKey);
                            }else{
                                callBack.onError(ALREADY_IN_GROUP, "Zaten bu gruba kayıtlı");
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
            callBack.onSuccess("Kişi, gruba başarıyla eklendi");
        }else {
            callBack.onError(NO_SELECTED_FRIEND, "Lütfen arkadaş seçiniz");
        }
    }

    public void removeFromGroup(final String selected_person_id, final Groups group, final DatabaseCallBack callBack){
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
                ArrayList<String> current_users_groups = (ArrayList<String>) snapshot.getValue();
                if (current_users_groups.contains(group_key)){
                    current_users_groups.remove(group_key);
                }else{
                    callBack.onError(ALREADY_REMOVED_FROM_GROUP, "Bu gruptan zaten silinmiş");
                }
                user_groups_ref.setValue(current_users_groups);

                int group_members_size = group.getGroup_members().size();
                if(group_members_size <= 2) {
                    group_reference.child(group_key).setValue(null);
                    group.removeFriend(selected_person_id);
                    String last_person_key = group.getGroup_members().get(0);
                    final DatabaseReference l_user_groups_ref = user_reference.child(last_person_key).child("groups");
                    l_user_groups_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                callBack.onError(VALUE_NOT_FOUND, "Burası boş");
                                return;
                            }
                            ArrayList<String> current_users_groups = (ArrayList<String>) snapshot.getValue();
                            if (current_users_groups.contains(group_key)){
                                current_users_groups.remove(group_key);
                            }else{
                                callBack.onError(ALREADY_REMOVED_FROM_GROUP, "Bu gruptan zaten silinmiş");
                            }
                            l_user_groups_ref.setValue(current_users_groups);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            callBack.onError(DATABASE_ERROR, error.getMessage());
                        }
                    });
                }else{
                    group.removeFriend(selected_person_id);
                    group_reference.child(group_key).setValue(group);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
        callBack.onSuccess("Gruptan başarıyla çıkarıldı");
    }

    public void deleteGroup(final Groups group, final DatabaseCallBack callBack){
        final String group_key = group.getKey();
        final ArrayList<String> group_members_id = group.getGroup_members();
        for(String friend_id: group_members_id){
            final DatabaseReference user_groups_ref = user_reference.child(friend_id).child("groups");
            user_groups_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<String> current_users_groups = (ArrayList<String>) snapshot.getValue();
                    if (current_users_groups.contains(group_key)){
                        current_users_groups.remove(group_key);
                    }else{
                        callBack.onError(ALREADY_REMOVED_FROM_GROUP, "Bu gruptan zaten silinmiş");
                    }
                    user_groups_ref.setValue(current_users_groups);
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
