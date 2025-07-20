package com.example.restaurantapplication.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.restaurantapplication.R
import com.example.restaurantapplication.data.model.CartManager
import com.example.restaurantapplication.databinding.FragmentCartBinding


class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private var cartGrandTotal: Double = 0.0 // ✅ Store grand total here

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set action bar title
        requireActivity().title = "Cart"

        renderCartItems()

        // Clear all cart...
        binding.btnClearCart.setOnClickListener {
            CartManager.clearCart(requireContext())
            renderCartItems()
        }

        //Click on Order Now...
        binding.btnOrderNow.setOnClickListener {
            val cartItems = CartManager.getCartItems(requireContext())

            if (cartItems.isEmpty()) {
                Toast.makeText(requireContext(), "Cart is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Prepare total and item list
            val itemList = mutableListOf<Map<String, Any>>()
            var totalItemCount = 0
            cartItems.forEach {
                val itemPrice = it.price.toInt()
                val quantity = it.count
                totalItemCount += quantity

                itemList.add(
                    mapOf(
                        "item_id" to it.id.toLong(),
                        "item_price" to itemPrice,
                        "item_quantity" to quantity
                    )
                )
            }
            val totalAmount = String.format("%.2f", cartGrandTotal) // ✅ use previously calculated grand total
//            Toast.makeText(requireContext(), "totalAmount: $totalAmount", Toast.LENGTH_SHORT).show()


            // Show loader
            binding.progressBar.visibility = View.VISIBLE
            binding.btnOrderNow.isEnabled = false

            Thread {
                val repository = com.example.restaurantapplication.data.RestaurantRepository()
                val result = repository.makePayment(totalAmount, cartItems.size, itemList)

                requireActivity().runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    binding.btnOrderNow.isEnabled = true

                    if (result) {
                        // ✅ Store last successful payment
                        com.example.restaurantapplication.data.model.LastPaymentManager.saveLastPayment(
                            context = requireContext(),
                            amount = totalAmount,
                            itemCount = totalItemCount,
                            items = itemList
                        )

                        showPaymentDialog("Payment Successful", "Your payment was processed successfully!", true)
                        CartManager.clearCart(requireContext())
                        renderCartItems()
                    } else {
                        showPaymentDialog("Payment Failed", "Something went wrong while processing your payment.", false)
                    }
                }
            }.start()
        }

    }

    private fun renderCartItems() {
        val cartItems = CartManager.getCartItems(requireContext())
        binding.llCartItems.removeAllViews()

        if (cartItems.isEmpty()) {
            val emptyText = TextView(requireContext()).apply {
                text = "Your cart is empty"
                textSize = 16f
                setPadding(16, 16, 16, 16)
            }
            binding.llCartItems.addView(emptyText)
            binding.tvTotal.visibility = View.GONE
        } else {
            var netTotal = 0.0

            cartItems.forEach { item ->
                val itemView = layoutInflater.inflate(R.layout.cart_item, binding.llCartItems, false)

                val tvItemName = itemView.findViewById<TextView>(R.id.tvItemName)
                val tvItemCount = itemView.findViewById<TextView>(R.id.tvItemCount)
                val tvItemPrice = itemView.findViewById<TextView>(R.id.tvItemPrice)

                val itemTotal = item.count * item.price.toInt()
                netTotal += itemTotal

                tvItemName.text = "${item.name} (${item.price})"
                tvItemCount.text = "x${item.count}"
                tvItemPrice.text = "₹%.2f".format(itemTotal.toDouble())

                binding.llCartItems.addView(itemView)
            }

            val cgst = netTotal * 0.025
            val sgst = netTotal * 0.025
            cartGrandTotal = netTotal + cgst + sgst // ✅ store totalAmount here

            // Display all totals
            binding.tvTotal.text = """
            Net Total: ₹%.2f
            CGST (2.5%%): ₹%.2f
            SGST (2.5%%): ₹%.2f
            Grand Total: ₹%.2f
        """.trimIndent().format(netTotal, cgst, sgst, cartGrandTotal)
        }
    }

    private fun showPaymentDialog(title: String, message: String, isSuccess: Boolean) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}