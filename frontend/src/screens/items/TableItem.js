import { Fragment } from 'react';
import { IconButton, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper } from '@mui/material';
import { Riple } from 'react-loading-indicators';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';

export default function TableItem({ handleEditOpen, handleDeleteOpen, loading, items }) {

    const columns = [
        { width: 20, label: 'Item Id', dataKey: 'itemid', numeric: true },
        { width: 50, label: 'Description', dataKey: 'itemDescription' },
        { width: 50, label: 'Category', dataKey: 'category' },
        { width: 30, label: 'HSN Code', dataKey: 'type' },
        { width: 30, label: 'Status', dataKey: 'status' },
        { width: 20, label: 'Price', dataKey: 'price' },
        { width: 50, label: 'Fulfillment', dataKey: 'fulfillment' },
        { width: 20, label: 'Action', dataKey: 'action', numeric: true },
    ];

    const rowContent = (row) => (
        <TableRow key={row.itemid}>
            {columns.map((column) => (
                <TableCell key={column.dataKey} align={column.numeric ? 'center' : 'left'}>
                    {column.dataKey === 'action' ? (
                        <>
                            <IconButton onClick={() => handleEditOpen(row)}><EditIcon /></IconButton>
                            <IconButton onClick={() => handleDeleteOpen(row)}><DeleteIcon /></IconButton>
                        </>
                    ) : column.dataKey === 'status' ? (row[column.dataKey]).toString().replace(/_/g, ' ') 
                    : column.dataKey === 'price' ? `₹${row[column.dataKey]}` 
                    : row[column.dataKey]}
                </TableCell>
            ))}
        </TableRow>
    );

    return (
        <Paper style={{ height: 680, width: 1200, display: 'flex', margin: '20px' }}>
            {loading && <Riple color="#803bec" size="large" />}
            {!loading && items.length > 0 ? (
                <TableContainer component={Paper}>
                    <Table sx={{ borderCollapse: 'separate', tableLayout: 'fixed' }}>
                        <TableHead>
                            <TableRow>
                                {columns.map((column) => (
                                    <TableCell
                                        key={column.dataKey}
                                        align={column.numeric ? 'center' : 'left'}
                                        style={{ width: column.width, backgroundColor: "black", color: "white", fontWeight: "Bold", textTransform: 'uppercase' }}
                                    >
                                        {column.label}
                                    </TableCell>
                                ))}
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {items.map((row) => rowContent(row))}
                        </TableBody>
                    </Table>
                </TableContainer>
            ) : (
                !loading && <p>No items found.</p>
            )}
        </Paper>
    );
}
