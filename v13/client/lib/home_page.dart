import 'package:flutter/material.dart';
import 'package:curved_navigation_bar/curved_navigation_bar.dart';
import 'order_page.dart';
import 'new_order_page.dart';
import 'profile_page.dart';
import 'constants.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  _HomePageState createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  int _currentIndex = 0;

  final List<Widget> _pages = [
    const OrderPage(), // Navigate to the OrderPage
    const NewOrderPage(), // Navigate to the NewOrderPage
    const ProfilePage(), // Navigate to the ProfilePage
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _pages[_currentIndex], // Display current screen based on index
      bottomNavigationBar: CurvedNavigationBar(
        items: const [
          Icon(Icons.list, color: Colors.white), // Icon for OrderPage
          Icon(Icons.add, color: Colors.white), // Icon for NewOrderPage
          Icon(Icons.person, color: Colors.white), // Icon for ProfilePage
        ],
        backgroundColor: Color(0xFFE1BEE7), // Light purple background
        color: Colors.purple.shade900, // Dark purple navigation bar
        animationDuration: const Duration(milliseconds: 200),
        animationCurve: Curves.bounceIn,
        index: _currentIndex,
        onTap: (index) {
          setState(() {
            _currentIndex = index;
          });
        },
      ),
    );
  }
}