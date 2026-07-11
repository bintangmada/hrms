const API_BASE_URL = import.meta.env.VITE_API_URL || 'https://api.bintangmada.web.id';

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface UserDto {
  id: number;
  username: string;
  email: string;
  role: string;
}

export interface EmployeeDto {
  id: number;
  nik: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  position: string;
  department: string;
  joinDate: string;
  salary: number;
  userId?: number;
  username?: string;
  createdBy?: string;
  updatedBy?: string;
  createdAt?: string;
  updatedAt?: string;
  status?: number;
}

export interface EmployeeRequest {
  nik: string;
  firstName: string;
  lastName?: string;
  email: string;
  phone?: string;
  position?: string;
  department?: string;
  joinDate?: string;
  salary?: number;
  userId?: number;
}

// Token management
export const getToken = (): string | null => localStorage.getItem('hrms_token');
export const setToken = (token: string): void => localStorage.setItem('hrms_token', token);
export const removeToken = (): void => localStorage.removeItem('hrms_token');

export const getCurrentUser = (): UserDto | null => {
  const userJson = localStorage.getItem('hrms_user');
  if (!userJson) return null;
  try {
    return JSON.parse(userJson);
  } catch (e) {
    return null;
  }
};

export const setCurrentUser = (user: UserDto): void => {
  localStorage.setItem('hrms_user', JSON.stringify(user));
};

export const removeCurrentUser = (): void => {
  localStorage.removeItem('hrms_user');
};

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const token = getToken();
  const headers = new Headers(options.headers || {});
  
  if (token) {
    headers.set('Authorization', `Bearer ${token}`);
  }
  if (!headers.has('Content-Type') && !(options.body instanceof FormData)) {
    headers.set('Content-Type', 'application/json');
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    let errorMsg = 'An unexpected error occurred';
    try {
      const errData = await response.json();
      errorMsg = errData.message || errorMsg;
    } catch (_) {
      // fallback if response isn't JSON
    }
    throw new Error(errorMsg);
  }

  return response.json() as Promise<T>;
}

export const api = {
  auth: {
    login: async (payload: any) => {
      const res = await request<ApiResponse<any>>('/api/v1/auth/login', {
        method: 'POST',
        body: JSON.stringify(payload),
      });
      if (res.success && res.data.token) {
        setToken(res.data.token);
        setCurrentUser({
          id: res.data.id || 0,
          username: res.data.username || payload.username,
          email: res.data.email || '',
          role: res.data.role || 'ROLE_STAFF'
        });
      }
      return res;
    },
    register: async (payload: any) => {
      return request<ApiResponse<UserDto>>('/api/v1/auth/register/staff', {
        method: 'POST',
        body: JSON.stringify(payload),
      });
    },
    confirmEmail: async (token: string) => {
      return request<ApiResponse<string>>(`/api/v1/auth/confirm-email?token=${token}`);
    },
  },
  employees: {
    getAll: async () => {
      const res = await request<ApiResponse<EmployeeDto[]>>('/api/v1/employees');
      return res.data;
    },
    getById: async (id: number) => {
      const res = await request<ApiResponse<EmployeeDto>>(`/api/v1/employees/${id}`);
      return res.data;
    },
    getByUserId: async (userId: number) => {
      const res = await request<ApiResponse<EmployeeDto>>(`/api/v1/employees/user/${userId}`);
      return res.data;
    },
    create: async (payload: EmployeeRequest) => {
      const res = await request<ApiResponse<EmployeeDto>>('/api/v1/employees', {
        method: 'POST',
        body: JSON.stringify(payload),
      });
      return res.data;
    },
    update: async (id: number, payload: EmployeeRequest) => {
      const res = await request<ApiResponse<EmployeeDto>>(`/api/v1/employees/${id}/update`, {
        method: 'POST',
        body: JSON.stringify(payload),
      });
      return res.data;
    },
    delete: async (id: number) => {
      const res = await request<ApiResponse<string>>(`/api/v1/employees/${id}/delete`, {
        method: 'POST',
      });
      return res.message;
    },
  },
  users: {
    getAll: async () => {
      const res = await request<ApiResponse<UserDto[]>>('/api/v1/users');
      return res.data;
    },
    getById: async (id: number) => {
      const res = await request<ApiResponse<UserDto>>(`/api/v1/users/${id}`);
      return res.data;
    },
    updateRoles: async (id: number, roleIds: number[]) => {
      const res = await request<ApiResponse<UserDto>>(`/api/v1/users/${id}/roles`, {
        method: 'POST',
        body: JSON.stringify({ roleIds }),
      });
      return res.data;
    }
  },
  roles: {
    getAll: async () => {
      const res = await request<ApiResponse<RoleDto[]>>('/api/v1/roles');
      return res.data;
    },
    getById: async (id: number) => {
      const res = await request<ApiResponse<RoleDto>>(`/api/v1/roles/${id}`);
      return res.data;
    },
    create: async (payload: RoleRequest) => {
      const res = await request<ApiResponse<RoleDto>>('/api/v1/roles', {
        method: 'POST',
        body: JSON.stringify(payload),
      });
      return res.data;
    },
    update: async (id: number, payload: RoleRequest) => {
      const res = await request<ApiResponse<RoleDto>>(`/api/v1/roles/${id}/update`, {
        method: 'POST',
        body: JSON.stringify(payload),
      });
      return res.data;
    },
    delete: async (id: number) => {
      const res = await request<ApiResponse<string>>(`/api/v1/roles/${id}/delete`, {
        method: 'POST',
      });
      return res.message;
    }
  },
  menus: {
    getAll: async () => {
      const res = await request<ApiResponse<MenuDto[]>>('/api/v1/menus');
      return res.data;
    }
  },
  roleMenus: {
    assign: async (payload: RoleMenuRequest) => {
      const res = await request<ApiResponse<RoleMenuResponse>>('/api/v1/role-menus/assign', {
        method: 'POST',
        body: JSON.stringify(payload),
      });
      return res.data;
    },
    getByRoleId: async (roleId: number) => {
      const res = await request<ApiResponse<RoleMenuResponse[]>>(`/api/v1/role-menus/role/${roleId}`);
      return res.data;
    },
    getMyPermissions: async () => {
      const res = await request<ApiResponse<UserPermissionResponse[]>>('/api/v1/role-menus/my-permissions');
      return res.data;
    }
  }
};

export interface RoleDto {
  id: number;
  name: string;
  description?: string;
  createdBy?: string;
  updatedBy?: string;
  createdAt?: string;
  updatedAt?: string;
  status?: number;
  deletedStatus?: number;
}

export interface RoleRequest {
  name: string;
  description?: string;
}

export interface MenuDto {
  id: number;
  name: string;
  code: string;
  path: string;
}

export interface RoleMenuResponse {
  id: number;
  roleId: number;
  menuId: number;
  canRead: number;
  canWrite: number;
  canDelete: number;
}

export interface RoleMenuRequest {
  roleId: number;
  menuId: number;
  canRead: number;
  canWrite: number;
  canDelete: number;
}

export interface UserPermissionResponse {
  menuId: number;
  menuName: string;
  menuCode: string;
  menuPath: string;
  canRead: number;
  canWrite: number;
  canDelete: number;
}

