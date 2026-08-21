import React, { useState, useEffect, useCallback } from 'react';
import type { Book, Category, BorrowRecord, NotificationItem, User } from './types';
import { bookApi, categoryApi, borrowApi, notificationApi, userApi } from './services/api';
import { AuthProvider, useAuth } from './context/AuthContext';
import { ToastProvider, useToast } from './context/ToastContext';
import { Header } from './components/common/Header';
import { Sidebar } from './components/common/Sidebar';
import { ToastContainer } from './components/common/ToastContainer';
import { AuthModal } from './components/auth/AuthModal';
import { ApiSettingsModal } from './components/auth/ApiSettingsModal';

import { LibrarianDashboard } from './components/librarian/LibrarianDashboard';
import { BookManagement } from './components/librarian/BookManagement';
import { BookModal } from './components/librarian/BookModal';
import { AddCopyModal } from './components/librarian/AddCopyModal';
import { CategoryManagement } from './components/librarian/CategoryManagement';
import { CategoryModal } from './components/librarian/CategoryModal';
import { BorrowManagement } from './components/librarian/BorrowManagement';
import { CreateBorrowModal } from './components/librarian/CreateBorrowModal';
import { ReturnBookModal } from './components/librarian/ReturnBookModal';
import { UserManagement } from './components/librarian/UserManagement';
import { UserModal } from './components/librarian/UserModal';

import { BorrowerDashboard } from './components/borrower/BorrowerDashboard';
import { BookCatalog } from './components/borrower/BookCatalog';
import { BorrowHistory } from './components/borrower/BorrowHistory';
import { NotificationsInbox } from './components/borrower/NotificationsInbox';
import { BorrowModal } from './components/borrower/BorrowModal';

import './App.css';

const MainApp: React.FC = () => {
  const { user, token } = useAuth();
  const { showToast } = useToast();

  // --- Workspace Tab State ---
  const [activeTab, setActiveTab] = useState<string>('dashboard');

  // --- Real Data State from Database (Initial Empty Arrays) ---
  const [books, setBooks] = useState<Book[]>([]);
  const [bookPage, setBookPage] = useState<number>(0);
  const [pageSize] = useState<number>(10);
  const [sizeOfPage, setSizeOfPage] = useState<number>(0);
  const [categories, setCategories] = useState<Category[]>([]);
  const [borrows, setBorrows] = useState<BorrowRecord[]>([]);
  const [borrowPage, setBorrowPage] = useState<number>(0);
  const [borrowPageSize] = useState<number>(10);
  const [borrowSizeOfPage, setBorrowSizeOfPage] = useState<number>(0);
  const [notifications, setNotifications] = useState<NotificationItem[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [userPage, setUserPage] = useState<number>(0);
  const [userPageSize] = useState<number>(10);
  const [userSizeOfPage, setUserSizeOfPage] = useState<number>(0);
  const [loading, setLoading] = useState<boolean>(false);

  // --- Modal Open States ---
  const [isAuthOpen, setIsAuthOpen] = useState<boolean>(false);
  const [isSettingsOpen, setIsSettingsOpen] = useState<boolean>(false);
  const [isBookModalOpen, setIsBookModalOpen] = useState<boolean>(false);
  const [editingBook, setEditingBook] = useState<Book | null>(null);
  const [isAddCopyOpen, setIsAddCopyOpen] = useState<boolean>(false);
  const [selectedBookForCopy, setSelectedBookForCopy] = useState<Book | null>(null);
  const [isCategoryModalOpen, setIsCategoryModalOpen] = useState<boolean>(false);
  const [editingCategory, setEditingCategory] = useState<Category | null>(null);
  const [isCreateBorrowOpen, setIsCreateBorrowOpen] = useState<boolean>(false);
  const [isReturnBookOpen, setIsReturnBookOpen] = useState<boolean>(false);
  const [selectedBorrowCode, setSelectedBorrowCode] = useState<string>('');
  const [isUserModalOpen, setIsUserModalOpen] = useState<boolean>(false);
  const [editingUser, setEditingUser] = useState<User | null>(null);
  const [isBorrowModalOpen, setIsBorrowModalOpen] = useState<boolean>(false);
  const [selectedBookForBorrow, setSelectedBookForBorrow] = useState<Book | null>(null);

  // Set default tab based on user role
  useEffect(() => {
    if (user?.role === 'LIBRARIAN') {
      setActiveTab('dashboard');
    } else {
      setActiveTab('home');
    }
  }, [user?.role]);

  // Fetch real data from backend Database REST APIs
  const fetchRealDatabaseData = useCallback(async () => {
    if (!token) {
      setBooks([]);
      setCategories([]);
      setBorrows([]);
      setNotifications([]);
      setUsers([]);
      return;
    }

    setLoading(true);
    try {
      const [fetchedBooks, fetchedCats, fetchedBorrows, fetchedNotifs, fetchedUsers] = await Promise.allSettled([
        bookApi.getAll(bookPage, pageSize),
        categoryApi.getAll(),
        user?.role === 'LIBRARIAN' ? borrowApi.getAll(borrowPage, borrowPageSize) : borrowApi.getMyHistory(borrowPage, borrowPageSize),
        notificationApi.getMyNotifications(),
        user?.role === 'LIBRARIAN' ? userApi.getAllUsers(userPage, userPageSize) : Promise.resolve({ page: 0, sizeOfPage: 0, content: [] }),
      ]);

      if (fetchedBooks.status === 'fulfilled' && fetchedBooks.value) {
        const pageData = fetchedBooks.value;
        setBooks(pageData.content || []);
        setBookPage(pageData.page ?? 0);
        setSizeOfPage(pageData.sizeOfPage ?? (pageData.content?.length || 0));
      }
      if (fetchedCats.status === 'fulfilled' && Array.isArray(fetchedCats.value)) {
        setCategories(fetchedCats.value);
      }
      let currentBorrows: BorrowRecord[] = [];
      if (fetchedBorrows.status === 'fulfilled' && fetchedBorrows.value) {
        const pageData = fetchedBorrows.value;
        currentBorrows = pageData.content || [];
        setBorrows(currentBorrows);
        setBorrowPage(pageData.page ?? 0);
        setBorrowSizeOfPage(pageData.sizeOfPage ?? (currentBorrows.length));
      }

      let notifsList: NotificationItem[] = [];
      if (fetchedNotifs.status === 'fulfilled' && Array.isArray(fetchedNotifs.value)) {
        notifsList = [...fetchedNotifs.value];
      }

      // Tự động tổng hợp thông báo quá hạn từ phiếu mượn nếu Kafka chưa đẩy sang NotificationService
      const overdueBorrows = currentBorrows.filter((b) => b.status === 'OVERDUE');
      overdueBorrows.forEach((b) => {
        const exists = notifsList.some(
          (n) => n.message.includes(b.borrowCode) || (n.title && n.title.includes(b.borrowCode))
        );
        if (!exists) {
          notifsList.unshift({
            id: b.id,
            notificationId: b.id,
            userId: b.borrowerId || user?.id || 1,
            title: 'CẢNH BÁO: Sách Quá Hạn Trả',
            message: `Sách "${b.bookTitle}" (Mã mượn: ${b.borrowCode}) đã quá hạn trả ngày ${b.dueDate}. Vui lòng mang sách tới quầy thủ thư để trả ngay!`,
            type: 'OVERDUE_WARNING',
            read: false,
            isRead: false,
            createdAt: b.dueDate,
          });
        }
      });

      setNotifications(notifsList);
      if (fetchedUsers.status === 'fulfilled' && fetchedUsers.value) {
        const pageData = fetchedUsers.value;
        if ('content' in pageData && Array.isArray(pageData.content)) {
          setUsers(pageData.content || []);
          setUserPage(pageData.page ?? 0);
          setUserSizeOfPage(pageData.sizeOfPage ?? (pageData.content?.length || 0));
        } else if (Array.isArray(pageData)) {
          setUsers(pageData as User[]);
        }
      }
    } catch (err: unknown) {
      console.warn('Backend REST API call error:', err);
    } finally {
      setLoading(false);
    }
  }, [token, user?.role, bookPage, pageSize, borrowPage, borrowPageSize, userPage, userPageSize]);

  const handleBorrowPageChange = async (newPage: number) => {
    if (newPage < 0) return;
    setLoading(true);
    try {
      const res = user?.role === 'LIBRARIAN'
        ? await borrowApi.getAll(newPage, borrowPageSize)
        : await borrowApi.getMyHistory(newPage, borrowPageSize);
      setBorrows(res.content || []);
      setBorrowPage(res.page);
      setBorrowSizeOfPage(res.sizeOfPage);
    } catch (err) {
      console.warn('Error fetching paginated borrows:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleBookPageChange = async (newPage: number) => {
    if (newPage < 0) return;
    setLoading(true);
    try {
      const res = await bookApi.getAll(newPage, pageSize);
      setBooks(res.content || []);
      setBookPage(res.page);
      setSizeOfPage(res.sizeOfPage);
    } catch (err) {
      console.warn('Error fetching paginated books:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleUserPageChange = async (newPage: number) => {
    if (newPage < 0) return;
    setLoading(true);
    try {
      const res = await userApi.getAllUsers(newPage, userPageSize);
      setUsers(res.content || []);
      setUserPage(res.page);
      setUserSizeOfPage(res.sizeOfPage);
    } catch (err) {
      console.warn('Error fetching paginated users:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRealDatabaseData();
  }, [fetchRealDatabaseData]);

  // --- Handlers for Book Operations ---
  const handleSaveBook = async (bookData: Partial<Book>) => {
    try {
      if (editingBook) {
        await bookApi.update(editingBook.id, bookData);
        showToast('Cập nhật đầu sách trong Database thành công!');
      } else {
        await bookApi.create(bookData);
        showToast('Tạo đầu sách mới trong Database thành công!');
      }
      setIsBookModalOpen(false);
      setEditingBook(null);
      fetchRealDatabaseData();
    } catch (err: unknown) {
      showToast((err as Error).message || 'Không thể kết nối lưu sách vào Database', 'error');
    }
  };

  const handleDeleteBook = async (id: number) => {
    if (window.confirm('Bạn có chắc chắn muốn xóa đầu sách này khỏi Database?')) {
      try {
        await bookApi.delete(id);
        showToast('Đã xóa đầu sách khỏi Database', 'warning');
        fetchRealDatabaseData();
      } catch (err: unknown) {
        showToast((err as Error).message || 'Xóa sách thất bại', 'error');
      }
    }
  };

  const handleAddCopies = async (bookId: number, count: number, note?: string) => {
    try {
      await bookApi.importBooks(bookId, count, note);
      showToast(`Đã nhập thêm ${count} bản sao vào Database`);
      setIsAddCopyOpen(false);
      fetchRealDatabaseData();
    } catch (err: unknown) {
      showToast((err as Error).message || 'Nhập bản sao thất bại', 'error');
    }
  };

  // --- Handlers for Category Operations ---
  const handleSaveCategory = async (catData: { id?: number; code?: string; name: string; description?: string }) => {
    try {
      if (catData.id) {
        await categoryApi.update(catData.id, catData);
        showToast(`Cập nhật danh mục "${catData.name}" trong Database thành công!`);
      } else {
        await categoryApi.create(catData);
        showToast(`Đã thêm danh mục "${catData.name}" vào Database`);
      }
      setIsCategoryModalOpen(false);
      setEditingCategory(null);
      fetchRealDatabaseData();
    } catch (err: unknown) {
      showToast((err as Error).message || 'Lưu thông tin danh mục thất bại', 'error');
    }
  };

  const handleDeleteCategory = async (categoryId: number) => {
    if (window.confirm(`Bạn có chắc chắn muốn xóa danh mục #${categoryId} khỏi Database?`)) {
      try {
        await categoryApi.delete(categoryId);
        showToast(`Đã xóa danh mục #${categoryId} khỏi Database`, 'warning');
        fetchRealDatabaseData();
      } catch (err: unknown) {
        showToast((err as Error).message || 'Xóa danh mục thất bại', 'error');
      }
    }
  };

  // --- Handlers for User Operations (UserService) ---
  const handleSaveUser = async (data: { userId?: number; fullName: string; phone: string }) => {
    try {
      if (data.userId) {
        await userApi.updateUser(data.userId, { fullName: data.fullName, phone: data.phone });
        showToast('Cập nhật thông tin người dùng thành công!');
      }
      setIsUserModalOpen(false);
      setEditingUser(null);
      fetchRealDatabaseData();
    } catch (err: unknown) {
      showToast((err as Error).message || 'Lưu thông tin người dùng thất bại', 'error');
    }
  };

  const handleDeleteUser = async (userId: number) => {
    if (window.confirm(`Bạn có chắc chắn muốn xóa người dùng #${userId} khỏi Database?`)) {
      try {
        await userApi.deleteUserAdmin(userId);
        showToast(`Đã xóa người dùng #${userId}`);
        fetchRealDatabaseData();
      } catch (err: unknown) {
        showToast((err as Error).message || 'Xóa người dùng thất bại', 'error');
      }
    }
  };

  const handleSearchUsers = async (keyword: string) => {
    try {
      const res = await userApi.searchUsers(keyword, 0, userPageSize);
      setUsers(res.content || []);
      setUserPage(res.page);
      setUserSizeOfPage(res.sizeOfPage);
      showToast(`Tìm thấy ${(res.content || []).length} người dùng phù hợp.`);
    } catch (err: unknown) {
      showToast((err as Error).message || 'Tìm kiếm người dùng thất bại', 'error');
    }
  };

  // --- Handlers for Borrow & Return Operations ---
  const handleCreateBorrow = async (data: { borrowerUsername: string; bookId: number; days: number }) => {
    try {
      const res = await borrowApi.create(data);
      showToast(`Tạo phiếu mượn ${res.borrowCode || ''} thành công!`);
      setIsCreateBorrowOpen(false);
      fetchRealDatabaseData();
    } catch (err: unknown) {
      showToast((err as Error).message || 'Tạo phiếu mượn thất bại', 'error');
    }
  };

  const handleReturnBook = async (borrowCode: string) => {
    try {
      await borrowApi.returnBook(borrowCode);
      showToast(`Đã hoàn tất nhận trả sách cho phiếu ${borrowCode}`);
      setIsReturnBookOpen(false);
      fetchRealDatabaseData();
    } catch (err: unknown) {
      showToast((err as Error).message || 'Thủ tục nhận trả thất bại', 'error');
    }
  };

  // --- Borrower Quick Borrow Modal & Confirmation ---
  const handleOpenBorrowModal = (book: Book) => {
    const avail = book.availableCopies ?? book.availableQuantity ?? 0;
    if (avail <= 0) {
      showToast('Sách này hiện tại đã hết bản sao sẵn có', 'error');
      return;
    }
    setSelectedBookForBorrow(book);
    setIsBorrowModalOpen(true);
  };

  const handleConfirmBorrow = async (book: Book, days: number, dueDateStr: string) => {
    try {
      const res = await borrowApi.create({
        borrowerUsername: user?.username || 'reader',
        bookId: book.id || book.bookId || 0,
        days,
        dueDate: dueDateStr,
      });

      showToast(`Đã mượn thành công sách "${book.title}". Mã phiếu: ${res.borrowCode || ''}. Hạn trả: ${dueDateStr}`);
      setIsBorrowModalOpen(false);
      setSelectedBookForBorrow(null);

      // Optimistically decrement available copies in state
      setBooks((prevBooks) =>
        prevBooks.map((b) => {
          if (b.id === book.id || b.bookId === book.id) {
            const curAvail = b.availableCopies ?? b.availableQuantity ?? 1;
            const nextAvail = Math.max(0, curAvail - 1);
            return { ...b, availableCopies: nextAvail, availableQuantity: nextAvail };
          }
          return b;
        })
      );

      // Refresh fresh data from Database server
      fetchRealDatabaseData();
    } catch (err: unknown) {
      showToast((err as Error).message || 'Yêu cầu mượn sách thất bại', 'error');
    }
  };

  // Dynamic Header Title
  const getHeaderTitle = () => {
    if (user?.role === 'LIBRARIAN') {
      switch (activeTab) {
        case 'dashboard':
          return 'Dashboard Tổng Quan Hệ Thống';
        case 'books':
          return 'Quản Lý Đầu Sách & Bản Sao';
        case 'categories':
          return 'Danh Mục Sách Thư Viện';
        case 'borrows':
          return 'Quản Lý Mượn & Trả Sách';
        case 'users':
          return 'Quản Lý Người Dùng & Độc Giả';
        default:
          return 'Trang Quản Trị Thủ Thư';
      }
    } else {
      switch (activeTab) {
        case 'home':
          return 'Trang Chủ Độc Giả';
        case 'catalog':
          return 'Tra Cứu & Đăng Ký Mượn Sách';
        case 'history':
          return 'Lịch Sử Mượn Trả Sách';
        case 'notifications':
          return 'Hộp Thư Thông Báo';
        default:
          return 'Trang Độc Giả';
      }
    }
  };

  const unreadNotifCount = notifications.filter((n) => !n.read).length;

  return (
    <div className="app-container">
      <ToastContainer />

      {/* Sidebar Component */}
      <Sidebar
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        unreadCount={unreadNotifCount}
        onOpenAuth={() => setIsAuthOpen(true)}
      />

      {/* Main Content Workspace */}
      <div className="main-wrapper">
        <Header
          title={getHeaderTitle()}
          onOpenSettings={() => setIsSettingsOpen(true)}
          onOpenAuth={() => setIsAuthOpen(true)}
        />

        <main className="content-body">
          {loading && (
            <div style={{ marginBottom: '16px', color: 'var(--primary-hover)', fontWeight: 600 }}>
              <i className="fa-solid fa-spinner fa-spin"></i> Đang tải dữ liệu thực từ Database server...
            </div>
          )}

          {/* LIBRARIAN PANELS */}
          {user?.role === 'LIBRARIAN' && (
            <>
              {activeTab === 'dashboard' && (
                <LibrarianDashboard
                  books={books}
                  borrows={borrows}
                  onNavigateTab={setActiveTab}
                  onOpenBorrowModal={() => setIsCreateBorrowOpen(true)}
                  onOpenReturnModal={() => setIsReturnBookOpen(true)}
                  onOpenBookModal={() => {
                    setEditingBook(null);
                    setIsBookModalOpen(true);
                  }}
                  onRefreshData={fetchRealDatabaseData}
                />
              )}

              {activeTab === 'books' && (
                <BookManagement
                  books={books}
                  categories={categories}
                  page={bookPage}
                  pageSize={pageSize}
                  sizeOfPage={sizeOfPage}
                  onPageChange={handleBookPageChange}
                  onOpenAddBook={() => {
                    setEditingBook(null);
                    setIsBookModalOpen(true);
                  }}
                  onOpenEditBook={(book) => {
                    setEditingBook(book);
                    setIsBookModalOpen(true);
                  }}
                  onDeleteBook={handleDeleteBook}
                  onOpenAddCopy={(book) => {
                    setSelectedBookForCopy(book);
                    setIsAddCopyOpen(true);
                  }}
                />
              )}

              {activeTab === 'categories' && (
                <CategoryManagement
                  categories={categories}
                  onOpenAddCategory={() => {
                    setEditingCategory(null);
                    setIsCategoryModalOpen(true);
                  }}
                  onOpenEditCategory={(cat) => {
                    setEditingCategory(cat);
                    setIsCategoryModalOpen(true);
                  }}
                  onDeleteCategory={handleDeleteCategory}
                />
              )}

              {activeTab === 'borrows' && (
                <BorrowManagement
                  borrows={borrows}
                  page={borrowPage}
                  pageSize={borrowPageSize}
                  sizeOfPage={borrowSizeOfPage}
                  onPageChange={handleBorrowPageChange}
                  onOpenCreateBorrow={() => setIsCreateBorrowOpen(true)}
                  onOpenReturnBook={(code) => {
                    setSelectedBorrowCode(code || '');
                    setIsReturnBookOpen(true);
                  }}
                />
              )}

              {activeTab === 'users' && (
                <UserManagement
                  users={users}
                  allBorrows={borrows}
                  page={userPage}
                  pageSize={userPageSize}
                  sizeOfPage={userSizeOfPage}
                  onPageChange={handleUserPageChange}
                  onOpenEditUser={(u) => {
                    setEditingUser(u);
                    setIsUserModalOpen(true);
                  }}
                  onDeleteUser={handleDeleteUser}
                  onSearchUsers={handleSearchUsers}
                />
              )}
            </>
          )}

          {/* BORROWER PANELS */}
          {user?.role !== 'LIBRARIAN' && (
            <>
              {activeTab === 'home' && (
                <BorrowerDashboard
                  books={books}
                  borrows={borrows}
                  onNavigateTab={setActiveTab}
                  onQuickBorrow={handleOpenBorrowModal}
                />
              )}

              {activeTab === 'catalog' && (
                <BookCatalog
                  books={books}
                  categories={categories}
                  page={bookPage}
                  pageSize={pageSize}
                  sizeOfPage={sizeOfPage}
                  onPageChange={handleBookPageChange}
                  onQuickBorrow={handleOpenBorrowModal}
                />
              )}

              {activeTab === 'history' && (
                <BorrowHistory
                  borrows={borrows}
                  page={borrowPage}
                  pageSize={borrowPageSize}
                  sizeOfPage={borrowSizeOfPage}
                  onPageChange={handleBorrowPageChange}
                />
              )}

              {activeTab === 'notifications' && (
                <NotificationsInbox
                  notifications={notifications}
                  onMarkAsRead={async (id) => {
                    try {
                      await notificationApi.markAsRead(id);
                      fetchRealDatabaseData();
                    } catch {
                      setNotifications((prev) =>
                        prev.map((n) => (n.id === id ? { ...n, read: true } : n))
                      );
                    }
                  }}
                />
              )}
            </>
          )}
        </main>
      </div>

      {/* ALL MODALS */}
      <AuthModal isOpen={isAuthOpen} onClose={() => setIsAuthOpen(false)} />
      <ApiSettingsModal isOpen={isSettingsOpen} onClose={() => setIsSettingsOpen(false)} />
      <BookModal
        isOpen={isBookModalOpen}
        editingBook={editingBook}
        categories={categories}
        onClose={() => setIsBookModalOpen(false)}
        onSave={handleSaveBook}
      />
      <AddCopyModal
        isOpen={isAddCopyOpen}
        book={selectedBookForCopy}
        onClose={() => setIsAddCopyOpen(false)}
        onAddCopies={handleAddCopies}
      />
      <CategoryModal
        isOpen={isCategoryModalOpen}
        editingCategory={editingCategory}
        onClose={() => setIsCategoryModalOpen(false)}
        onSave={handleSaveCategory}
      />
      <CreateBorrowModal
        isOpen={isCreateBorrowOpen}
        books={books}
        onClose={() => setIsCreateBorrowOpen(false)}
        onCreateBorrow={handleCreateBorrow}
      />
      <ReturnBookModal
        isOpen={isReturnBookOpen}
        selectedBorrowCode={selectedBorrowCode}
        onClose={() => {
          setIsReturnBookOpen(false);
          setSelectedBorrowCode('');
        }}
        onReturnBook={handleReturnBook}
      />
      <UserModal
        isOpen={isUserModalOpen}
        editingUser={editingUser}
        onClose={() => setIsUserModalOpen(false)}
        onSave={handleSaveUser}
      />
      <BorrowModal
        isOpen={isBorrowModalOpen}
        book={selectedBookForBorrow}
        onClose={() => {
          setIsBorrowModalOpen(false);
          setSelectedBookForBorrow(null);
        }}
        onConfirmBorrow={handleConfirmBorrow}
      />
    </div>
  );
};

export function App() {
  return (
    <ToastProvider>
      <AuthProvider>
        <MainApp />
      </AuthProvider>
    </ToastProvider>
  );
}

export default App;
