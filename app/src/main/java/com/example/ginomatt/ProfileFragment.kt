package com.example.ginomatt

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var btnLogout: Button
    private lateinit var etFriendSearch: EditText
    private lateinit var btnAddFriend: Button
    private lateinit var rvFriends: RecyclerView

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var friendsAdapter: FriendsAdapter
    private var searchResults = mutableListOf<UserModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Views
        tvUserName = view.findViewById(R.id.tvUserName)
        tvUserEmail = view.findViewById(R.id.tvUserEmail)
        btnLogout = view.findViewById(R.id.btnLogout)
        etFriendSearch = view.findViewById(R.id.etFriendSearch)
        btnAddFriend = view.findViewById(R.id.btnAddFriend)
        rvFriends = view.findViewById(R.id.rvFriends)

        // Set user info
        val currentUser = auth.currentUser
        if (currentUser != null) {
            tvUserName.text = currentUser.displayName ?: "User Name"
            tvUserEmail.text = currentUser.email ?: "user@example.com"
        }

        // Logout
        btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // RecyclerView setup
        friendsAdapter = FriendsAdapter(searchResults) { user ->
            addFriend(user)
        }
        rvFriends.layoutManager = LinearLayoutManager(requireContext())
        rvFriends.adapter = friendsAdapter

        // Search button
        btnAddFriend.setOnClickListener {
            val emailInput = etFriendSearch.text.toString().trim().lowercase()
            if (emailInput.isEmpty()) {
                Toast.makeText(requireContext(), "Enter email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            searchUserByEmail(emailInput)
        }

        return view
    }

    private fun searchUserByEmail(emailLower: String) {
        db.collection("users")
            .whereEqualTo("email_lower", emailLower) // use lowercase field
            .get()
            .addOnSuccessListener { documents ->
                searchResults.clear()
                if (!documents.isEmpty) {
                    for (doc in documents) {
                        val userEmail = doc.getString("email") ?: ""
                        // Skip self
                        if (userEmail.lowercase() != auth.currentUser?.email?.lowercase()) {
                            val user = UserModel(
                                uid = doc.id,
                                name = doc.getString("name") ?: "No Name",
                                email = userEmail
                            )
                            searchResults.add(user)
                        }
                    }
                }

                if (searchResults.isEmpty()) {
                    Toast.makeText(requireContext(), "No user found", Toast.LENGTH_SHORT).show()
                }
                friendsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addFriend(user: UserModel) {
        val currentUserId = auth.currentUser?.uid ?: return
        val friendRef = db.collection("users").document(currentUserId)
            .collection("friends").document(user.uid)

        friendRef.set(user)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "${user.name} added as friend", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to add friend", Toast.LENGTH_SHORT).show()
            }
    }
}

// RecyclerView Adapter
class FriendsAdapter(
    private val friends: List<UserModel>,
    private val onAddClick: (UserModel) -> Unit
) : RecyclerView.Adapter<FriendsAdapter.FriendViewHolder>() {

    inner class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvFriendName)
        val tvEmail: TextView = itemView.findViewById(R.id.tvFriendEmail)
        val btnAdd: Button = itemView.findViewById(R.id.btnAddFriendItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend, parent, false)
        return FriendViewHolder(view)
    }

    override fun getItemCount(): Int = friends.size

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val user = friends[position]
        holder.tvName.text = user.name
        holder.tvEmail.text = user.email
        holder.btnAdd.setOnClickListener { onAddClick(user) }
    }
}

// User model
data class UserModel(
    val uid: String = "",
    val name: String = "",
    val email: String = ""
)
