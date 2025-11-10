






import Foundation
import StoreKit

@available(iOS 15.0, *)
@objc public class IAPStoreKit: NSObject {
    @objc public static let shared = IAPStoreKit()
    
    private var products: [Product] = []
    private var updateListenerTask: Task<Void, Error>?
    

    private var purchaseStateCallback: (([String: Bool]) -> Void)?
    
    @objc public override init() {
        super.init()
        updateListenerTask = listenForTransactions()
        

        Task {
            await loadProducts()
        }
    }
    
    deinit {
        updateListenerTask?.cancel()
    }
    

    
    @objc public func setPurchaseStateCallback(_ callback: @escaping ([String: Bool]) -> Void) {
        self.purchaseStateCallback = callback
    }
    

    
    private func loadProducts() async {



        let productIds = [
            "com.bonbasses.timer10",
            "com.bonbasses.canvaspack"
        ]
        
        do {
            products = try await Product.products(for: productIds)

            

            await updatePurchaseStates()
        } catch {

        }
    }
    

    
    @objc public func getProductsAsync(completion: @escaping ([NSDictionary]) -> Void) {
        Task {
            if products.isEmpty {
                await loadProducts()
            }
            
            var productsArray: [NSDictionary] = []
            
            for product in products {
                let isPurchased = await checkPurchased(productId: product.id)
                
                let dict: NSDictionary = [
                    "id": product.id,
                    "title": product.displayName,
                    "description": product.description,
                    "price": product.displayPrice,
                    "isPurchased": isPurchased
                ]
                productsArray.append(dict)
            }
            
            completion(productsArray)
        }
    }
    

    
    @objc public func isPurchasedAsync(productId: String, completion: @escaping (Bool) -> Void) {
        Task {
            let purchased = await checkPurchased(productId: productId)
            completion(purchased)
        }
    }
    
    private func checkPurchased(productId: String) async -> Bool {
        for await result in Transaction.currentEntitlements {
            if case .verified(let transaction) = result {
                if transaction.productID == productId {
                    return true
                }
            }
        }
        return false
    }
    

    
    @objc public func purchaseAsync(productId: String, completion: @escaping (Bool, String?) -> Void) {
        Task {
            if products.isEmpty {
                await loadProducts()
            }
            
            guard let product = products.first(where: { $0.id == productId }) else {
                completion(false, "Product not found")
                return
            }
            
            do {
                let result = try await product.purchase()
                
                switch result {
                case .success(let verification):
                    switch verification {
                    case .verified(let transaction):
                        await transaction.finish()
                        

                        await updatePurchaseStates()
                        
                        completion(true, nil)
                        
                    case .unverified:
                        completion(false, "Transaction verification failed")
                    }
                    
                case .userCancelled:
                    completion(false, "cancelled")
                    
                case .pending:
                    completion(false, "Purchase pending")
                    
                @unknown default:
                    completion(false, "Unknown error")
                }
            } catch {
                completion(false, error.localizedDescription)
            }
        }
    }
    

    
    @objc public func restorePurchasesAsync(completion: @escaping (Bool, String?) -> Void) {
        Task {
            do {
                try await AppStore.sync()
                

                await updatePurchaseStates()
                
                completion(true, nil)
            } catch {
                completion(false, error.localizedDescription)
            }
        }
    }
    

    
    private func updatePurchaseStates() async {
        var purchaseStates: [String: Bool] = [:]
        
        let productIds = [
            "com.bonbasses.timer10",
            "com.bonbasses.typewriter",
            "com.bonbasses.focus"
        ]
        
        for productId in productIds {
            purchaseStates[productId] = await checkPurchased(productId: productId)
        }
        

        DispatchQueue.main.async {
            self.purchaseStateCallback?(purchaseStates)
        }
    }
    

    
    private func listenForTransactions() -> Task<Void, Error> {
        return Task.detached {
            for await result in Transaction.updates {
                do {
                    let transaction = try self.checkVerified(result)
                    await transaction.finish()
                    

                    await self.updatePurchaseStates()
                } catch {

                }
            }
        }
    }
    
    private func checkVerified<T>(_ result: VerificationResult<T>) throws -> T {
        switch result {
        case .unverified:
            throw IAPError.failedVerification
        case .verified(let safe):
            return safe
        }
    }
}



enum IAPError: Error {
    case productNotFound
    case userCancelled
    case pending
    case unknown
    case failedVerification
}
