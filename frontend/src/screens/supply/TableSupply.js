import { Fragment } from 'react';
import { IconButton, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper } from '@mui/material';
import { Riple } from 'react-loading-indicators';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';

export default function TableSupply({ handleEditOpen, handleDeleteOpen, loading, supply }) {

    const columns = [
        { width: 80, label: 'Item', dataKey: 'itemId', numeric: true },
        { width: 80, label: 'Location', dataKey: 'locationId', numeric: true },
        { width: 80, label: 'Supply Type', dataKey: 'supplyType', numeric: true },
        { width: 40, label: 'Quantity', dataKey: 'quantity', numeric: true },
        { width: 20, label: 'Action', dataKey: 'action', numeric: true },
    ];

    const rowContent = (row) => (
        <TableRow key={row.itemId}>
            {columns.map((column) => (
                <TableCell key={column.dataKey} align={'center'}>
                    {column.dataKey === 'action' ? (
                        <>
                            <IconButton onClick={() => handleEditOpen(row)}><EditIcon /></IconButton>
                            <IconButton onClick={() => handleDeleteOpen(row)}><DeleteIcon /></IconButton>
                        </>
                    ) : row[column.dataKey]}
                </TableCell>
            ))}
        </TableRow>
    );

    return (
        <Paper style={{ height: 640, width: 1200, display: 'flex', margin: '20px' }}>
            {loading && <Riple color="#803bec" size="large" />}
            {!loading && supply.length > 0 ? (
                <TableContainer component={Paper}>
                    <Table sx={{ borderCollapse: 'separate', tableLayout: 'fixed' }}>
                        <TableHead>
                            <TableRow>
                                {columns.map((column) => (
                                    <TableCell
                                        key={column.dataKey}
                                        align={'center'}
                                        style={{
                                            width: column.width,
                                            backgroundColor: "black",
                                            color: "white",
                                            fontWeight: "Bold",
                                            textTransform: 'uppercase'
                                        }}
                                    >
                                        {column.label}
                                    </TableCell>
                                ))}
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {supply.map((row) => rowContent(row))}
                        </TableBody>
                    </Table>
                </TableContainer>
            ) : (
                !loading && <p>No supply found.</p>
            )}
        </Paper>
    );
}
