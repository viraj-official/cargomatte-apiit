import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:flutter/services.dart';
import 'constants.dart';

class OrderPage extends StatefulWidget {
  const OrderPage({super.key});

  @override
  _OrderPageState createState() => _OrderPageState();
}

class _OrderPageState extends State<OrderPage> {
  List orders = [];
  TextEditingController searchController = TextEditingController();


  @override
  void initState() {
    super.initState();
    fetchOrders();
  }

  Future<void> fetchOrders() async {
    final response = await http.get(
      Uri.parse("http://localhost:8082/api/customers/order"),
      headers: {
        "Authorization": "Bearer ${Constants.authToken}",
        "Content-Type": "application/json",
      },
    );

    if (response.statusCode == 200) {
      setState(() {
        orders = json.decode(response.body);
      });
    }
  }

  Future<void> searchOrder(String trackingId) async {
    if (trackingId.isEmpty) {
      fetchOrders();
      return;
    }

    final response = await http.get(
      Uri.parse("http://localhost:8082/api/customers/track/$trackingId"),
      headers: {
        "Authorization": "Bearer ${Constants.authToken}",
        "Content-Type": "application/json",
      },
    );

    if (response.statusCode == 200) {
      setState(() {
        orders = [json.decode(response.body)];
      });
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Order not found!")),
      );
    }
  }

  Future<void> updateOrder(int orderId, String newAddress, String newDeliveryDate) async {
    final response = await http.put(
      Uri.parse("http://localhost:8082/api/customers/order/$orderId"),
      headers: {
        "Authorization": "Bearer ",
        "Content-Type": "application/json",
      },
      body: jsonEncode({
        "shipmentAddress": newAddress,
        "deliveryDate": newDeliveryDate,
      }),
    );

    if (response.statusCode == 200) {
      fetchOrders();
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Order updated successfully!")),
      );
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Failed to update order!")),
      );
    }
  }

  void copyToClipboard(String text) {
    Clipboard.setData(ClipboardData(text: text));
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text("Copied to clipboard!")),
    );
  }

  void showEditOrderDialog(Map order) {
    TextEditingController addressController = TextEditingController(text: order['shipmentAddress']);
    TextEditingController dateController = TextEditingController(text: order['deliveryDate'] ?? "");

    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: Text('Edit Order #${order['id']}'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(
                controller: addressController,
                decoration: const InputDecoration(labelText: 'Shipment Address'),
              ),
              TextField(
                controller: dateController,
                decoration: const InputDecoration(labelText: 'Delivery Date (YYYY-MM-DD)'),
              ),
            ],
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: const Text('Cancel'),
            ),
            TextButton(
              onPressed: () {
                updateOrder(order['id'], addressController.text, dateController.text);
                Navigator.pop(context);
              },
              child: const Text('Save'),
            ),
          ],
        );
      },
    );
  }

  Color getStatusColor(String status) {
    switch (status) {
      case 'CREATED': return Colors.blue;
      case 'PENDING': return Colors.orange;
      case 'COMPLETED': return Colors.green;
      case 'CANCELED': return Colors.red;
      case 'SHIPPED': return Colors.purple;
      case 'DELIVERED': return Colors.teal;
      case 'RETURNED': return Colors.brown;
      default: return Colors.grey;
    }
  }

  Widget buildOrderDetail(String title, String? value) {
    return value != null && value.isNotEmpty
        ? Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Text('$title: $value', style: const TextStyle(fontSize: 14)),
    )
        : const SizedBox();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Orders'),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            TextField(
              controller: searchController,
              decoration: InputDecoration(
                labelText: "Enter Tracking ID",
                suffixIcon: IconButton(
                  icon: const Icon(Icons.clear),
                  onPressed: () {
                    searchController.clear();
                    fetchOrders();
                  },
                ),
              ),
              onSubmitted: (value) {
                searchOrder(value);
              },
            ),
            const SizedBox(height: 16),
            Expanded(
              child: ListView.builder(
                itemCount: orders.length,
                itemBuilder: (context, index) {
                  final order = orders[index];
                  bool canEdit = !['COMPLETED', 'CANCELED', 'DELIVERED'].contains(order['status']);

                  return ListTile(
                    leading: CircleAvatar(
                      backgroundColor: getStatusColor(order['status']),
                      radius: 6,
                    ),
                    title: Text('Order #${order['id']}'),
                    subtitle: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text('Status: ${order['status']}', style: TextStyle(color: getStatusColor(order['status']))),
                        Text('Delivery Date: ${order['deliveryDate'] ?? "N/A"}'),
                      ],
                    ),
                    trailing: canEdit
                        ? IconButton(
                      icon: const Icon(Icons.edit),
                      onPressed: () => showEditOrderDialog(order),
                    )
                        : null,
                    onTap: () {
                      showDialog(
                        context: context,
                        builder: (context) {
                          return AlertDialog(
                            title: Text('Order #${order['id']} Details'),
                            content: SingleChildScrollView(
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  buildOrderDetail('Shipment Address', order['shipmentAddress']),
                                  Row(
                                    children: [
                                      Expanded(
                                        child: Text(
                                          'Tracking Number: ${order['trackingNumber']}',
                                          overflow: TextOverflow.ellipsis,
                                        ),
                                      ),
                                      IconButton(
                                        icon: const Icon(Icons.copy, size: 20),
                                        onPressed: () => copyToClipboard(order['trackingNumber']),
                                      ),
                                    ],
                                  ),
                                  buildOrderDetail('Payment Method', order['paymentMethod']),
                                  buildOrderDetail('Payment Status', order['paymentStatus']),
                                  buildOrderDetail('Shipping Fees', "\$${order['shippingFees']}"),
                                  buildOrderDetail('Weight', "${order['weight']} kg"),
                                  buildOrderDetail('Breakable', order['isBreakable'] ? "Yes" : "No"),
                                  buildOrderDetail('Special Instructions', order['specialInstructions']),
                                ],
                              ),
                            ),
                            actions: [
                              TextButton(
                                onPressed: () => Navigator.pop(context),
                                child: const Text('Close'),
                              ),
                            ],
                          );
                        },
                      );
                    },
                  );
                },
              ),
            ),
          ],
        ),
      ),
    );
  }
}
