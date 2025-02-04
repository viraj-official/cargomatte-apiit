import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'constants.dart';

class NewOrderPage extends StatefulWidget {
  const NewOrderPage({super.key});

  @override
  _NewOrderPageState createState() => _NewOrderPageState();
}

class _NewOrderPageState extends State<NewOrderPage> {
  final _formKey = GlobalKey<FormState>();

  final TextEditingController _addressLine1Controller =
  TextEditingController(text: "123 Main Street");
  final TextEditingController _addressLine2Controller =
  TextEditingController(text: "Apt 4B");
  final TextEditingController _cityController =
  TextEditingController(text: "New York");
  final TextEditingController _postedCodeController =
  TextEditingController(text: "82100");

  final TextEditingController _deliveryDateController =
  TextEditingController(text: "2025-02-25");
  final TextEditingController _weightController =
  TextEditingController(text: "16.00");
  final TextEditingController _specialInstructionsController =
  TextEditingController(text: "dfghj");
  final TextEditingController _orderNotesController =
  TextEditingController(text: "Leave package at the front door.");

  bool _isBreakable = false;
  String _paymentMethod = "CREDIT_CARD"; // Default
  final String _paymentStatus = "PAID";
  List<String> _paymentMethods = ["CREDIT_CARD", "CASH", "PAYPAL"];
  String? _selectedPaymentMethod;

  DateTime? _selectedDate;

  Future<void> _submitOrder() async {
    if (_formKey.currentState!.validate()) {
      Map<String, dynamic> orderRequest = {
        "paymentMethod": _selectedPaymentMethod ?? _paymentMethod,
        "paymentStatus": _paymentStatus,
        "shipmentAddress":
        "${_addressLine1Controller.text}, ${_addressLine2Controller.text}, ${_cityController.text}, ${_postedCodeController.text}",
        "deliveryDate": "${_deliveryDateController.text}T00:00:00",
        "weight": double.tryParse(_weightController.text) ?? 0.0,
        "isBreakable": _isBreakable,
        "specialInstructions": _specialInstructionsController.text,
        "orderNotes": _orderNotesController.text,
      };

      final response = await http.post(
        Uri.parse('http://localhost:8082/api/customers/order'),
        headers: {
          "Content-Type": "application/json",
          "Authorization":
          "Bearer ${Constants.authToken}"
        },
        body: jsonEncode(orderRequest),
      );

      if (response.statusCode == 200 || response.statusCode == 201) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("Order Created Successfully!")),
        );
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("Order Creation Failed!")),
        );
      }
    }
  }

  Future<void> _selectDeliveryDate() async {
    DateTime? picked = await showDatePicker(
      context: context,
      initialDate: _selectedDate ?? DateTime.now(),
      firstDate: DateTime(2020),
      lastDate: DateTime(2101),
    );

    if (picked != null) {
      setState(() {
        // Keep only the date part (yyyy-MM-dd) and store it as a string
        _selectedDate = picked;
        _deliveryDateController.text =
        "${_selectedDate!.year}-${_selectedDate!.month.toString().padLeft(2, '0')}-${_selectedDate!.day.toString().padLeft(2, '0')}";
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.deepPurple,
        title: const Text(
          'New Order',
          style: TextStyle(fontWeight: FontWeight.bold, fontSize: 24),
        ),
      ),
      body: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 16),
        child: Form(
          key: _formKey,
          child: SingleChildScrollView(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                _buildTextFormField(_addressLine1Controller, 'Address Line 1'),
                _buildTextFormField(_addressLine2Controller, 'Address Line 2'),
                _buildTextFormField(_cityController, 'City'),
                _buildTextFormField(_postedCodeController, 'Posted Code'),
                GestureDetector(
                  onTap: _selectDeliveryDate,
                  child: AbsorbPointer(
                    child: _buildTextFormField(
                      _deliveryDateController,
                      'Delivery Date (Pick a Date)',
                      keyboardType: TextInputType.datetime,
                    ),
                  ),
                ),
                _buildTextFormField(
                  _weightController,
                  'Weight (kg)',
                  keyboardType: TextInputType.number,
                  maxLength: 5,
                ),
                _buildPaymentMethodDropdown(),
                _buildBreakableSwitch(),
                _buildTextFormField(_specialInstructionsController,
                    'Special Instructions',
                    maxLines: 3),
                _buildTextFormField(
                    _orderNotesController, 'Order Notes', maxLines: 5),
                const SizedBox(height: 20),
                ElevatedButton(
                  onPressed: _submitOrder,
                  style: ElevatedButton.styleFrom(
                    padding: const EdgeInsets.symmetric(vertical: 18, horizontal: 40), backgroundColor: Colors.deepPurple, // Background color
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(30), // Rounded corners
                    ),
                    textStyle: const TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                      color: Colors.white
                    ),
                    elevation: 5, // Shadow for depth
                  ),
                  child: const Text('Create Order'),
                ),
                const SizedBox(height: 20),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildTextFormField(TextEditingController controller, String label,
      {TextInputType keyboardType = TextInputType.text, int maxLines = 1, int? maxLength}) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 16),
      child: TextFormField(
        controller: controller,
        decoration: InputDecoration(
          labelText: label,
          labelStyle: const TextStyle(fontWeight: FontWeight.bold),
          border: OutlineInputBorder(
            borderRadius: BorderRadius.circular(12),
            borderSide: const BorderSide(color: Colors.deepPurple, width: 1),
          ),
          focusedBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(12),
            borderSide: const BorderSide(color: Colors.deepPurple, width: 2),
          ),
        ),
        keyboardType: keyboardType,
        maxLines: maxLines,
        maxLength: maxLength,
        validator: (value) {
          if (value == null || value.isEmpty) {
            return '$label is required';
          }
          if (keyboardType == TextInputType.number &&
              value.isNotEmpty &&
              double.tryParse(value) == null) {
            return 'Please enter a valid number';
          }
          return null;
        },
      ),
    );
  }

  Widget _buildPaymentMethodDropdown() {
    return Padding(
      padding: const EdgeInsets.only(bottom: 16),
      child: DropdownButtonFormField<String>(
        value: _selectedPaymentMethod ?? _paymentMethod,
        items: _paymentMethods.map((method) {
          String displayMethod;
          switch (method) {
            case "CREDIT_CARD":
              displayMethod = "Credit Card";
              break;
            case "CASH":
              displayMethod = "Cash";
              break;
            case "PAYPAL":
              displayMethod = "PayPal";
              break;
            default:
              displayMethod = method;
          }
          return DropdownMenuItem<String>(
            value: method,
            child: Text(displayMethod),
          );
        }).toList(),
        onChanged: (value) {
          setState(() {
            _selectedPaymentMethod = value;
          });
        },
        decoration: InputDecoration(
          labelText: "Payment Method",
          labelStyle: const TextStyle(fontWeight: FontWeight.bold),
          border: OutlineInputBorder(
            borderRadius: BorderRadius.circular(12),
            borderSide: const BorderSide(color: Colors.deepPurple, width: 1),
          ),
          focusedBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(12),
            borderSide: const BorderSide(color: Colors.deepPurple, width: 2),
          ),
        ),
      ),
    );
  }

  Widget _buildBreakableSwitch() {
    return Padding(
      padding: const EdgeInsets.only(bottom: 16),
      child: SwitchListTile(
        title: const Text("Is Breakable", style: TextStyle(fontWeight: FontWeight.bold)),
        value: _isBreakable,
        onChanged: (bool value) {
          setState(() {
            _isBreakable = value;
          });
        },
        activeColor: Colors.deepPurple,
        contentPadding: EdgeInsets.zero,
      ),
    );
  }
}
