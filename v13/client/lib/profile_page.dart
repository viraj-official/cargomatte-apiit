import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';
import 'login_page.dart';
import 'constants.dart';

class ProfilePage extends StatefulWidget {
  const ProfilePage({super.key});

  @override
  State<ProfilePage> createState() => _ProfilePageState();
}

class _ProfilePageState extends State<ProfilePage> {
  Map<String, dynamic>? userData;
  bool isLoading = true;

  @override
  void initState() {
    super.initState();
    _fetchUserDetails();
  }

  Future<void> _fetchUserDetails() async {
    final prefs = await SharedPreferences.getInstance();
    String? token = prefs.getString("auth_token");

    if (token == null) {
      _logout();
      return;
    }

    const url = 'http://localhost:8082/api/customers/update';

    try {
      final response = await http.put(
        Uri.parse(url),
        headers: {
          "Content-Type": "application/json",
          "Authorization": "Bearer $token",
        },
        body: jsonEncode({}),
      );

      if (response.statusCode == 200) {
        setState(() {
          userData = jsonDecode(response.body);
          isLoading = false;
        });
      } else {
        throw Exception("Failed to load data");
      }
    } catch (e) {
      print("Error fetching user details: $e");
      setState(() {
        isLoading = false;
      });
    }
  }

  Future<void> _logout() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove("auth_token"); // Clear the stored token
    if (mounted) {
      Navigator.pushAndRemoveUntil(
        context,
        MaterialPageRoute(builder: (context) => const LoginPage()),
            (route) => false,
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    if (isLoading) {
      return const Scaffold(
        body: Center(child: CircularProgressIndicator()),
      );
    }

    if (userData == null) {
      return const Scaffold(
        body: Center(child: Text("Failed to load user data")),
      );
    }

    String displayName = (userData!["displayName"] as String?)?.isNotEmpty == true
        ? userData!["displayName"]
        : "${userData!["firstName"]} ${userData!["lastName"]}";

    return Scaffold(
      appBar: AppBar(
        title: const Text('Profile'),
        centerTitle: true,
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: _logout,
            tooltip: "Sign Out",
          ),
        ],
      ),
      body: Center(
        child: SingleChildScrollView(
          child: Padding(
            padding: const EdgeInsets.all(16.0),
            child: Column(
              children: [
                const CircleAvatar(radius: 50, child: Icon(Icons.person, size: 50)),
                const SizedBox(height: 20),
                Text(
                  displayName,
                  style: const TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 10),
                _buildProfileInfo("Email", userData!["email"]),
                _buildProfileInfo("Phone", userData!["phoneNumber"]),
                _buildProfileInfo("Address", userData!["address"]),
                _buildProfileInfo("Loyalty Points", userData!["loyaltyPoints"].toString()),
                _buildProfileInfo("Payment Method", userData!["defaultPaymentMethod"]),
                const SizedBox(height: 20),
                ElevatedButton(
                  onPressed: () => _showUpdateProfileDialog(context),
                  child: const Text('Edit Profile'),
                ),
                const SizedBox(height: 20),
                ElevatedButton(
                  onPressed: _logout,
                  style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
                  child: const Text('Sign Out', style: TextStyle(color: Colors.white)),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildProfileInfo(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8.0),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 120,
            child: Text(
              "$label:",
              style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
            ),
          ),
          const SizedBox(width: 10),
          Expanded(
            child: Text(
              value,
              style: const TextStyle(fontSize: 16),
            ),
          ),
        ],
      ),
    );
  }

  void _showUpdateProfileDialog(BuildContext context) {
    TextEditingController firstNameController = TextEditingController(text: userData!["firstName"]);
    TextEditingController lastNameController = TextEditingController(text: userData!["lastName"]);
    TextEditingController displayNameController = TextEditingController(text: userData!["displayName"]);
    TextEditingController phoneController = TextEditingController(text: userData!["phoneNumber"]);
    TextEditingController addressController = TextEditingController(text: userData!["address"]);

    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text("Update Profile"),
          content: SingleChildScrollView(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                TextField(controller: firstNameController, decoration: const InputDecoration(labelText: "First Name")),
                TextField(controller: lastNameController, decoration: const InputDecoration(labelText: "Last Name")),
                TextField(controller: displayNameController, decoration: const InputDecoration(labelText: "Display Name")),
                TextField(controller: phoneController, decoration: const InputDecoration(labelText: "Phone Number")),
                TextField(controller: addressController, decoration: const InputDecoration(labelText: "Address")),
              ],
            ),
          ),
          actions: [
            TextButton(onPressed: () => Navigator.pop(context), child: const Text("Cancel")),
            ElevatedButton(
              onPressed: () async {
                final updatedData = {
                  ...userData!,
                  "firstName": firstNameController.text,
                  "lastName": lastNameController.text,
                  "displayName": displayNameController.text,
                  "phoneNumber": phoneController.text,
                  "address": addressController.text,
                };

                final response = await _updateProfile(updatedData);
                if (response?.statusCode == 200) {
                  setState(() => userData = updatedData);
                  ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text("Profile updated!")));
                  Navigator.pop(context);
                } else {
                  ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text("Update failed!")));
                }
              },
              child: const Text("Save"),
            ),
          ],
        );
      },
    );
  }

  Future<http.Response?> _updateProfile(Map<String, dynamic> updatedData) async {
    final prefs = await SharedPreferences.getInstance();
    String? token = prefs.getString("auth_token");

    if (token == null) {
      _logout();
      return null;
    }

    const url = 'http://localhost:8082/api/customers/update';
    return http.put(Uri.parse(url),
        headers: {
          "Content-Type": "application/json",
          "Authorization": "Bearer $token",
        }, body: jsonEncode(updatedData));
  }
}
