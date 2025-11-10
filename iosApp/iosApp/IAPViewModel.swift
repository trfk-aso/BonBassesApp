






import Foundation
import StoreKit
import Combine

@available(iOS 15.0, *)
@MainActor
class IAPViewModel: ObservableObject {
    static let shared = IAPViewModel()
    
    @Published var products: [Product] = []
    @Published var purchasedProductIDs = Set<String>()
    



    private let productIDs = [
        "com.bonbasses.timer10",
        "com.bonbasses.canvaspack"
    ]
    
    private var updateListenerTask: Task<Void, Error>?
    
    init() {
        updateListenerTask = listenForTransactions()
        
        Task {
            await loadProducts()
            await updatePurchasedProducts()
        }
    }
    
    deinit {
        updateListenerTask?.cancel()
    }
    

    
    func loadProducts() async {
        do {
            products = try await Product.products(for: productIDs)

        } catch {

        }
    }
    

    
    func updatePurchasedProducts() async {
        var newPurchasedIDs = Set<String>()
        
        for await result in Transaction.currentEntitlements {
            if case .verified(let transaction) = result {
                newPurchasedIDs.insert(transaction.productID)
            }
        }
        
        purchasedProductIDs = newPurchasedIDs

    }
    
    func isPurchased(_ productID: String) -> Bool {
        return purchasedProductIDs.contains(productID)
    }
    

    
    func purchase(_ product: Product) async throws -> Transaction? {
        let result = try await product.purchase()
        
        switch result {
        case .success(let verification):
            let transaction = try checkVerified(verification)
            await transaction.finish()
            await updatePurchasedProducts()

            return transaction
            
        case .userCancelled:

            return nil
            
        case .pending:

            return nil
            
        @unknown default:

            return nil
        }
    }
    

    
    func restorePurchases() async {
        await updatePurchasedProducts()
    }
    

    
    private func listenForTransactions() -> Task<Void, Error> {
        return Task.detached {
            for await result in Transaction.updates {
                do {
                    let transaction = try await self.checkVerified(result)
                    await transaction.finish()
                    await self.updatePurchasedProducts()
                } catch {

                }
            }
        }
    }
    
    private func checkVerified<T>(_ result: VerificationResult<T>) throws -> T {
        switch result {
        case .unverified:
            throw StoreError.failedVerification
        case .verified(let safe):
            return safe
        }
    }
}

enum StoreError: Error {
    case failedVerification
}
